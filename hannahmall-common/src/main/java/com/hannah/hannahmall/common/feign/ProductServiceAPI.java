package com.hannah.hannahmall.common.feign;

import com.hannah.hannahmall.common.feign.vo.*;
import com.hannah.hannahmall.common.utils.R;
import com.hannah.hannahmall.common.utils.ResultBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "hannahmall-product")
@Component
public interface ProductServiceAPI {

    @RequestMapping("/product/skuinfo/info/{skuId}")
    public ResultBody<SkuInfoVO> info(@PathVariable("skuId") Long skuId);

    @PostMapping("product/skuinfo/list")
    public ResultBody<List<SkuInfoVO>> list(@RequestBody List<Long> skuIds);

    @RequestMapping("/product/skuinfo/getskuAttrsBySkuId")
    public ResultBody<List<String>> getskuAttrsBySkuId(@RequestParam("skuId") Long skuId);


    @PostMapping("/product/skuinfo/getSkuPriceBySkuIds")
    public ResultBody<List<SkuInfoPriceVO>> getSkuPriceBySkuIds(@RequestBody List<Long> skuIds);
    @PostMapping("product/skuinfo/getOrderItemsBySkuIds")
    List<OrderItemVO> getOrderItemsBySkuIds(@RequestBody List<Long> skuIds);

    @GetMapping("/product/skuinfo/attrInfo/{attrId}")
    public AttrRespVo attrInfo(@PathVariable("attrId") Long attrId);
    @PostMapping("/product/skuinfo/brands")
    List<BrandVo> brands(@RequestBody List<Long> brandIds);
    @PostMapping("/product/skuinfo/category")
    CategoryVO getByCategory3Id(@RequestBody Long catId);
}
