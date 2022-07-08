package com.hannah.hannahmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;


import com.hannah.hannahmall.common.feign.vo.WareHasStockVO;
import com.hannah.hannahmall.common.feign.vo.WareSkuLockVO;
import com.hannah.hannahmall.common.model.mq.to.OrderEntityPayedTO;
import com.hannah.hannahmall.common.model.mq.to.OrderEntityReleaseTO;
import com.hannah.hannahmall.common.model.mq.to.WareStockDelayTO;

import com.hannah.hannahmall.common.utils.PageUtils;
import com.hannah.hannahmall.common.utils.ResultBody;

import com.hannah.hannahmall.ware.entity.WareSkuEntity;


import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author rclin
 * @email rclin@hannah.com
 * @date 2020-06-07 11:15:53
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<WareHasStockVO> hasStock(List<Long> skuIds);

    ResultBody<Integer> hasStock(Long skuId);

    ResultBody wareSkuLock(WareSkuLockVO wareSkuLockVO);

    void stockRelease(WareStockDelayTO wareStockDelayTO);

    void stockRelease(OrderEntityReleaseTO orderEntityReleaseTO);

    void stockReduce(OrderEntityPayedTO orderEntityPayedTO);
    void addStock(Long skuId, Long wareId, Integer skuNum);
}

