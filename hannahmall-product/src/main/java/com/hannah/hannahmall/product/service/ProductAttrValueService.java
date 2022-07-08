package com.hannah.hannahmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hannah.hannahmall.common.utils.PageUtils;
import com.hannah.hannahmall.product.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author rclin
 * @email rclin@hannah.com
 * @date 2020-06-07 01:12:52
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveProductAttr(List<ProductAttrValueEntity> collect);

    List<ProductAttrValueEntity> baseAttrListforspu(Long spuId);
    void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> entities);
}

