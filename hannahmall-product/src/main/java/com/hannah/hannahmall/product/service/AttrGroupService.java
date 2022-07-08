package com.hannah.hannahmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hannah.hannahmall.common.utils.PageUtils;
import com.hannah.hannahmall.product.entity.AttrGroupEntity;
import com.hannah.hannahmall.product.vo.AttrGroupWithAttrsVo;
import com.hannah.hannahmall.product.vo.SpuItemAttrGroupVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author rclin
 * @email rclin@hannah.com
 * @date 2020-06-07 01:12:52
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long categoryId);

    AttrGroupEntity getAttrGroupEntityById(Long attrGroupId);

    List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId);

    List<AttrGroupEntity> getListByAttrGroupId(Long attrGroupId);

    List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId);
}

