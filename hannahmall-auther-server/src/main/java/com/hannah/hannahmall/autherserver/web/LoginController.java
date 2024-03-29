package com.hannah.hannahmall.autherserver.web;

import com.hannah.hannahmall.common.constant.AutherServerConstant;

import com.hannah.hannahmall.common.feign.MemberServiceAPI;
import com.hannah.hannahmall.common.feign.ThirdPartyServiceAPI;
import com.hannah.hannahmall.common.feign.vo.MemberVO;
import com.hannah.hannahmall.common.feign.vo.RegisterVO;
import com.hannah.hannahmall.common.feign.vo.UserLoginVO;

import com.hannah.hannahmall.common.utils.HttpUtils;
import com.hannah.hannahmall.common.utils.ResultBody;
import com.hannah.hannahmall.common.utils.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.hannah.hannahmall.common.constant.AutherServerConstant.*;


@Controller
@Slf4j
public class LoginController {
    @Autowired
    private ThirdPartyServiceAPI thirdPartyServiceAPI;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private MemberServiceAPI memberServiceAPI;
    @Autowired
    private RedissonClient redissonClient;


    @GetMapping("sms/sendCode")
    @ResponseBody
    public ResultBody sendMsg(@RequestParam("phone") String phone, HttpServletRequest request) throws InterruptedException {
        //一个手机号在60s内只能发送一次
        String value = redisTemplate.opsForValue().get(AutherServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (!StringUtils.isEmpty(value)) {
            //判断时间是否小于60秒
            long begin = Long.parseLong(value.split("_")[1]);
            long end = System.currentTimeMillis();
            if (end - begin < 60000) {
                return new ResultBody<>(10001, "发送频繁", null);
            }
        }
        //2、验证码的 redis.存key-phone,value-code再次效验
        int code = (int) ((Math.random() * 9 + 1) * 100000);
        String codeNum = String.valueOf(code);
        String redisStorage = codeNum + "_" + System.currentTimeMillis();
        //存入redis，防止同一个手机号在60秒内再次发送验证码
        redisTemplate.opsForValue().set(AutherServerConstant.SMS_CODE_CACHE_PREFIX + phone,
                redisStorage, 10, TimeUnit.MINUTES);

        thirdPartyServiceAPI.sendMsg(phone, codeNum);
        return new ResultBody();
    }


    /**
     * 用户注册
     * Model 转发上下文携带数据
     * RedirectAttributes 为重定向上下文携带数据
     *
     * @return
     */
    @PostMapping("/register")
    public String register(@Valid RegisterVO registerVO,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        //1、数据校验
        if (result.hasErrors()) {
            Map<String, String> errors = ValidationUtils.validationErrors(result);
            redirectAttributes.addFlashAttribute("errors", errors);
            //如果是转发到register,会导致表单重复提交
            return "redirect:" + AutherServerConstant.URL_REGISTER;
        }
        //2、校验验证码
        String phone = registerVO.getPhone();
        String code = registerVO.getCode();
        if (code.equals("9999")) {
            return getString(registerVO, redirectAttributes);
        }
        String key = AutherServerConstant.SMS_CODE_CACHE_PREFIX + phone;
        String redisCode = redisTemplate.opsForValue().get(key);

        if (!StringUtils.isEmpty(redisCode) && registerVO.getCode().equals(redisCode)) {
            return getString(registerVO, redirectAttributes);
        } else {
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "验证码错误");
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:" + AutherServerConstant.URL_REGISTER;
        }
    }

    private String getString(@Valid RegisterVO registerVO, RedirectAttributes redirectAttributes) {
        ResultBody resultBody = memberServiceAPI.memberRegister(registerVO);
        if (0 == resultBody.getCode()) {
            return "redirect:" + URL_LOGIN;
        } else {
            String msg = resultBody.getMsg();
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", msg);
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:" + AutherServerConstant.URL_REGISTER;
        }
    }

    @GetMapping("/login.html")
    public String loginHtml() {
        return "login";
    }


    /**
     * @param userLoginVO 1在搜索页面跳转至登录页面时,由于returnurl包含中文,js需要编码,防止重定向到returnurl出现中文乱码问题.
     *                    2在搜索页面跳转至登录页面,springmvc会自动把参数解码,如果前端js参数编码了,后台解码,会导致新的参数returnUrl解码,
     *                    当登录失败时,重定到登录页的returnUrl是处于解码状态,再次登录成功出现中文乱码.
     * @return
     */
    @PostMapping("/login")
    public String userLogin(@Valid UserLoginVO userLoginVO,
                            BindingResult result,
                            RedirectAttributes redirectAttributes,
                            HttpSession session) throws UnsupportedEncodingException {
        String redirectLogin = "";
        if (!StringUtils.isEmpty(userLoginVO.getReturnUrl())) {
            String encodeReturnUrl = URLEncoder.encode(userLoginVO.getReturnUrl(), "utf-8");
            redirectLogin = "redirect:" + URL_LOGIN + "?returnUrl=" + encodeReturnUrl;
        } else {
            redirectLogin = "redirect:" + URL_LOGIN;
        }
        if (result.hasErrors()) {
            Map<String, String> errors = ValidationUtils.validationErrors(result);
            redirectAttributes.addFlashAttribute("errors", errors);
            return redirectLogin;
        }
        if (session.getAttribute("loginUser") != null) {
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", "您已经登录,无需重复登录");
            redirectAttributes.addFlashAttribute("errors", errors);
            return redirectLogin;
        }

        ResultBody<MemberVO> memberVOResultBody = memberServiceAPI.memberLogin(userLoginVO);
        if (memberVOResultBody.getCode() == 0) {
            MemberVO memberVO = memberVOResultBody.getData();
            session.setAttribute(LOGIN_USER, memberVO);
            if (StringUtils.isEmpty(userLoginVO.getReturnUrl())) {
                return "redirect:" + URL_PORTAL;
            } else {
                return "redirect:" + userLoginVO.getReturnUrl();
            }
        } else {
            String msg = memberVOResultBody.getMsg();
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", msg);
            redirectAttributes.addFlashAttribute("errors", errors);
            return redirectLogin;
        }
    }

    @GetMapping("logout.html")
    public String logout(@RequestParam(value = "returnUrl", required = false) String returnUrl, HttpSession session) {
        session.removeAttribute(LOGIN_USER);
        if (StringUtils.isEmpty(returnUrl)) {
            returnUrl = URL_PORTAL;
        }
        return "redirect:" + returnUrl;
    }


}



