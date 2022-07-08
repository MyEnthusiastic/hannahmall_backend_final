package com.hannah.hannahmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.hannah.hannahmall.common.feign.vo.OrderItemVO;
import com.hannah.hannahmall.common.feign.vo.SkuInfoPriceVO;
import com.hannah.hannahmall.common.utils.PageUtils;
import com.hannah.hannahmall.common.utils.ResultBody;

import com.hannah.hannahmall.product.entity.SkuInfoEntity;
import com.hannah.hannahmall.product.vo.SkuItemVo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * sku信息
 *
 * @author rclin
 * @email rclin@hannah.com
 * @date 2020-06-07 01:12:52
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    SkuItemVo getSkuItem(Long skuId) throws ExecutionException, InterruptedException;
    SkuItemVo getSkuItemNoFuture(Long skuId) ;

    ResultBody<List<String>> getskuAttrsBySkuId(Long skuId);

    ResultBody<List<SkuInfoPriceVO>> getSkuPriceBySkuIds(List<Long> skuIds);

    List<OrderItemVO> getOrderItemsBySkuIds(List<Long> skuIds);

    void saveSkuInfo(SkuInfoEntity skuInfoEntity);
}

