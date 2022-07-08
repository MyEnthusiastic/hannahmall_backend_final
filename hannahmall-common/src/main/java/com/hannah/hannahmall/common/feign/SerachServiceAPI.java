package com.hannah.hannahmall.common.feign;

import com.hannah.hannahmall.common.feign.vo.SkuEsModel;

import com.hannah.hannahmall.common.utils.ResultBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("hannahmall-search")
public interface SerachServiceAPI {
    @PostMapping("search/save/product")
    public ResultBody productUp(@RequestBody List<SkuEsModel> skuEsModels);


}
