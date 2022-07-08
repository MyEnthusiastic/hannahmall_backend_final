package com.hannah.hannahmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hannah.hannahmall.common.utils.PageUtils;
import com.hannah.hannahmall.product.entity.BrandEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌
 *
 * @author rclin
 * @email rclin@hannah.com
 * @date 2020-06-07 01:12:52
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void delete(List<Long> asList);
}

