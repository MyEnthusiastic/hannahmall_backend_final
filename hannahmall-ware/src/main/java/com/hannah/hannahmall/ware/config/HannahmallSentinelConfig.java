package com.hannah.hannahmall.ware.config;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.hannah.hannahmall.common.exception.HannahmallExceptinCodeEnum;
import com.hannah.hannahmall.common.utils.ResultBody;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Configuration
public class HannahmallSentinelConfig {

    public HannahmallSentinelConfig() {

        WebCallbackManager.setUrlBlockHandler(new UrlBlockHandler() {
            @Override
            public void blocked(HttpServletRequest request, HttpServletResponse response, BlockException ex) throws IOException {
                ResultBody resultBody=new ResultBody(HannahmallExceptinCodeEnum.TOOMANG_REQUEST_EXCEPTION);
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json");
                response.getWriter().write(JSON.toJSONString(resultBody));

            }
        });

    }

}
