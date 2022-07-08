package com.hannah.hannahmall.product.dao;

import com.hannah.hannahmall.product.entity.CategoryBrandRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 品牌分类关联
 * 
 * @author rclin
 * @email rclin@hannah.com
 * @date 2020-06-07 01:12:52
 */
@Mapper
public interface CategoryBrandRelationDao extends BaseMapper<CategoryBrandRelationEntity> {
	
}
