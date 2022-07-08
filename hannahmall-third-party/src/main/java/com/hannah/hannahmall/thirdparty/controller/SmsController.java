package com.hannah.hannahmall.thirdparty.controller;

import com.hannah.hannahmall.common.feign.ThirdPartyServiceAPI;
import com.hannah.hannahmall.common.utils.ResultBody;

import com.hannah.hannahmall.thirdparty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("thirdparty")
public class SmsController implements ThirdPartyServiceAPI {
    @Autowired
    private SmsComponent smsComponent;

    @GetMapping("/sendMsg")
    public ResultBody sendMsg(@RequestParam("phone") String phone, @RequestParam("code") String code) {
        return smsComponent.sendMessage(code, phone);
    }


}
