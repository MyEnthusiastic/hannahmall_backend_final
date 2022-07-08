package com.hannah.hannahmall.ware.dao;

import com.hannah.hannahmall.ware.entity.PurchaseEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 采购信息
 * 
 * @author rclin
 * @email rclin@hannah.com
 * @date 2020-06-07 11:15:53
 */
@Mapper
public interface PurchaseDao extends BaseMapper<PurchaseEntity> {
	
}
