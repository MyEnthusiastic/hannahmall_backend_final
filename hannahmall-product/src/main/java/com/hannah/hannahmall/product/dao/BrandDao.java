package com.hannah.hannahmall.product.dao;

import com.hannah.hannahmall.product.entity.BrandEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 品牌
 *
 * @author rclin
 * @email rclin@hannah.com
 * @date 2020-06-07 01:12:52
 */
@Mapper
public interface BrandDao extends BaseMapper<BrandEntity> {
    @Select("SELECT name FROM pms_brand WHERE brand_id=#{brandId};")
    String selectBrandNameById(@Param("brandId") Long brandId);

}
