package com.hannah.hannahmall.product.dao;

import com.hannah.hannahmall.product.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hannah.hannahmall.product.vo.SkuItemSaleAttrVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author rclin
 * @email rclin@hannah.com
 * @date 2020-06-07 01:12:52
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<SkuItemSaleAttrVo> getSaleAttrBySpuId(@Param("spuId") Long spuId);
    List<String> getSkuSaleAttrValuesAsStringList(@Param("skuId") Long skuId);
}
