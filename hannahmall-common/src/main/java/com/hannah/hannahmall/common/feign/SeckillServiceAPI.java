package com.hannah.hannahmall.common.feign;

import com.hannah.hannahmall.common.feign.fallback.SeckillServiceAPIFallBack;
import com.hannah.hannahmall.common.feign.vo.SecSessionSkuVO;
import com.hannah.hannahmall.common.feign.vo.SeckillSkuVO;


import com.hannah.hannahmall.common.utils.ResultBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "hannahmall-seckill",fallback = SeckillServiceAPIFallBack.class )
@Component
public interface SeckillServiceAPI {

    /**
     * 根据skuId查询商品是否参加秒杀活动
     * @param skuId
     * @return
     */
    @GetMapping(value = "seckill/getSecSessionSkuVOBySkuId/{skuId}")
    ResultBody<SecSessionSkuVO> getSecSessionSkuVOBySkuId(@PathVariable("skuId") Long skuId);

    @GetMapping(value = "seckill/getCurrentSecSessionSkuVOS")
    ResultBody<List<SecSessionSkuVO>> getCurrentSecSessionSkuVOS();
}
