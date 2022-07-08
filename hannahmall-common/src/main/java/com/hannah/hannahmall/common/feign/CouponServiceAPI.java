package com.hannah.hannahmall.common.feign;

import com.hannah.hannahmall.common.feign.vo.SkuReductionVO;
import com.hannah.hannahmall.common.feign.vo.SpuBoundVO;
import com.hannah.hannahmall.common.utils.ResultBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 优惠价服务
 */
@FeignClient(value = "hannahmall-coupon")
public interface CouponServiceAPI {

    @PostMapping("/coupon/spubounds/save")
    void saveSpuBounds(SpuBoundVO spuBoundVO);
    @PostMapping("/coupon/spubounds/saveSkuFullReduction")
    void saveSkuReduction(SkuReductionVO skuReductionVO);
    @GetMapping("/coupon/spubounds/uploadSecKillProductLast3Days")
    void uploadSecKillProductLast3Days();



}
