package com.hannah.hannahmall.common.feign;

import com.hannah.hannahmall.common.utils.ResultBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "hannahmall-third-party")
public interface ThirdPartyServiceAPI {


    @GetMapping("thirdparty/sendMsg")
    public ResultBody sendMsg(@RequestParam("phone") String phone, @RequestParam("code") String code);

}
