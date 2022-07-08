package com.hannah.hannahmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hannah.hannahmall.common.utils.PageUtils;
import com.hannah.hannahmall.ware.entity.WareOrderTaskDetailEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author rclin
 * @email rclin@hannah.com
 * @date 2020-06-07 11:15:53
 */
public interface WareOrderTaskDetailService extends IService<WareOrderTaskDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

