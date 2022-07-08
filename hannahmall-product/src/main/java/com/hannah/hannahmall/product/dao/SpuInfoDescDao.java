package com.hannah.hannahmall.product.dao;

import com.hannah.hannahmall.product.entity.SpuInfoDescEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息介绍
 *
 * @author rclin
 * @email rclin@hannah.com
 * @date 2020-06-07 01:12:52
 */
@Mapper
public interface SpuInfoDescDao extends BaseMapper<SpuInfoDescEntity> {
    /**
     * map <saveEntity,></>
     * @param spuInfoDescEntity
     */
    void saveEntity(@Param("spuInfoDescEntity") SpuInfoDescEntity spuInfoDescEntity);

}
