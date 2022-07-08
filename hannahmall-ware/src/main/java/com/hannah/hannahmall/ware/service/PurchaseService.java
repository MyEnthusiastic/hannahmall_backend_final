package com.hannah.hannahmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hannah.hannahmall.common.utils.PageUtils;
import com.hannah.hannahmall.ware.entity.PurchaseEntity;
import com.hannah.hannahmall.ware.vo.MergeVo;
import com.hannah.hannahmall.ware.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author rclin
 * @email rclin@hannah.com
 * @date 2020-06-07 11:15:53
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);
    /**
     * 查询未领取的采购单
     * @param params
     * @return
     */
    PageUtils queryPageUnreceive(Map<String, Object> params);

    /**
     * 合并采购需求
     * @param mergeVo
     */
    void mergePurchase(MergeVo mergeVo);

    /**
     * 领取采购单
     * @param ids
     */
    void received(List<Long> ids);

    /**
     * 完成采购单
     * @param doneVo
     */
    void done(PurchaseDoneVo doneVo);
}

