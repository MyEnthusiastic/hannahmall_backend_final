package com.hannah.hannahmall.common.feign;


import com.hannah.hannahmall.common.feign.vo.WareSkuLockVO;
import com.hannah.hannahmall.common.utils.R;
import com.hannah.hannahmall.common.utils.ResultBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("hannahmall-ware")
public interface WareServiceAPI {
    @GetMapping("ware/waresku/hasStock/{skuId}")
    public ResultBody<Integer> hasStockById(@PathVariable("skuId") Long skuId);

    @PostMapping("ware/waresku/hasStock")
    public R hasStock(@RequestBody List<Long> skuIds);

    @PostMapping("ware/waresku/wareSkuLock")
    public ResultBody wareSkuLock(@RequestBody WareSkuLockVO wareSkuLockVO);
}
