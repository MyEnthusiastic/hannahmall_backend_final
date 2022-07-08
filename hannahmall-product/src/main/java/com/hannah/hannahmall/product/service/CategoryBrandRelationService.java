package com.hannah.hannahmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hannah.hannahmall.common.utils.PageUtils;
import com.hannah.hannahmall.product.entity.BrandEntity;
import com.hannah.hannahmall.product.entity.CategoryBrandRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author rclin
 * @email rclin@hannah.com
 * @date 2020-06-07 01:12:52
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageByBrandId(Map<String, Object> params, Long brandId);

    List<CategoryBrandRelationEntity> getListByBrandId(Long brandId);

    List<BrandEntity> getBrandsByCatId(Long catId);

    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    void deleteByBrandIds(List<Long> asList);
}

