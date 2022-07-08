package com.hannah.hannahmall.product.dao;

import com.hannah.hannahmall.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hannah.hannahmall.product.vo.SpuItemAttrGroupItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 *
 * @author rclin
 * @email rclin@hannah.com
 * @date 2020-06-07 01:12:52
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    List<SpuItemAttrGroupItem> getAttrGroupWithAttrsBySpuId(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);

}
