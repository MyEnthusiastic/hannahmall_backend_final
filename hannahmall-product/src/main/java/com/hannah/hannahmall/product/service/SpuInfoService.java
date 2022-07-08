package com.hannah.hannahmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hannah.hannahmall.common.utils.PageUtils;
import com.hannah.hannahmall.product.entity.SpuInfoEntity;
import com.hannah.hannahmall.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author rclin
 * @email rclin@hannah.com
 * @date 2020-06-07 01:12:52
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void up(Long spuId);

    void savesupInfo(SpuSaveVo vo);

    void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);
}

