package com.hannah.hannahmall.search.controller;

import com.hannah.hannahmall.common.exception.HannahmallExceptinCodeEnum;

import com.hannah.hannahmall.common.feign.vo.SkuEsModel;
import com.hannah.hannahmall.common.utils.ResultBody;
import com.hannah.hannahmall.common.feign.SerachServiceAPI;

import com.hannah.hannahmall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/search/save")
@RestController
@Slf4j
public class ElasticSearchSaveController implements SerachServiceAPI {

    @Autowired
    private ProductSaveService productSaveService;

    /**
     * 商品上架
     * @param skuEsModels
     * @return
     */
    @PostMapping("/product")
    public ResultBody productUp(@RequestBody List<SkuEsModel> skuEsModels)  {
        boolean b= false;
        try {
            b = productSaveService.productUp(skuEsModels);

        } catch (Exception e) {
            log.error("商品上架错误:{}",e.getMessage());
        }
        if (b){ return new ResultBody(0,"success",null);
        }
        return  new  ResultBody(HannahmallExceptinCodeEnum.PRODUCT_UP_ERROR,null);

    }


}
