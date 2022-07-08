package com.hannah.hannahmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hannah.hannahmall.common.utils.PageUtils;
import com.hannah.hannahmall.ware.entity.PurchaseDetailEntity;

import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author rclin
 * @email rclin@hannah.com
 * @date 2020-06-07 11:15:53
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);
    List<PurchaseDetailEntity> listDetailByPurchaseId(Long id);
}

