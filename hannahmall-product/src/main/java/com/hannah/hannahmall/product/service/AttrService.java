package com.hannah.hannahmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hannah.hannahmall.common.utils.PageUtils;
import com.hannah.hannahmall.product.entity.AttrEntity;
import com.hannah.hannahmall.product.vo.AttrGroupRelationVo;
import com.hannah.hannahmall.common.feign.vo.AttrRespVo;
import com.hannah.hannahmall.common.feign.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author rclin
 * @email rclin@hannah.com
 * @date 2020-06-07 01:12:52
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType);

    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);

    List<AttrEntity> getRelationAttr(Long attrGroupId);
    void updateAttrById(AttrVo attr);

    AttrRespVo getAttrInfo(Long attrId);

    void deleteRelation(AttrGroupRelationVo[] vos);

    void removeRelationByIds(List<Long> asList);
    void saveAttr(AttrVo attr);
}

