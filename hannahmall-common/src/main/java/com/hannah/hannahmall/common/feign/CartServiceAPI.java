package com.hannah.hannahmall.common.feign;

import com.hannah.hannahmall.common.feign.vo.CartItemVO;

import com.hannah.hannahmall.common.utils.ResultBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "hannahmall-cart")
public interface CartServiceAPI {
    @GetMapping("/cart/getCartListByUid")
    public ResultBody<List<CartItemVO>> getCartListByUid(@RequestParam("uid") String uid);
}
