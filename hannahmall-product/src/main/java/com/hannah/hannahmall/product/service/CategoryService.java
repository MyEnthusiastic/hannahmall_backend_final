package com.hannah.hannahmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hannah.hannahmall.common.feign.vo.CategoryVO;
import com.hannah.hannahmall.common.utils.PageUtils;
import com.hannah.hannahmall.product.entity.CategoryEntity;
import com.hannah.hannahmall.product.vo.Catalog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author rclin
 * @email rclin@hannah.com
 * @date 2020-06-07 01:12:52
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeMenuByIds(List<Long> asList);

    List<CategoryEntity> findFirstCategory();

    Map<String, List<Catalog2Vo>> getCatalog();

    Long[] findCatelogPath(Long catelogId);

    void updateByIds(List<CategoryEntity> asList);

    void saveAndRefreshCache(CategoryEntity category);

    void updateByIdAndRefreshCache(CategoryEntity category);

    CategoryVO getByCategory3Id(Long cat3Id);
}

