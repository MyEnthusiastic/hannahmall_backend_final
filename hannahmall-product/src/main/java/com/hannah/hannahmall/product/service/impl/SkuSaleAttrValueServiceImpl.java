package com.hannah.hannahmall.product.service.impl;

import com.hannah.hannahmall.product.vo.SkuItemSaleAttrVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hannah.hannahmall.common.utils.PageUtils;
import com.hannah.hannahmall.common.utils.Query;

import com.hannah.hannahmall.product.dao.SkuSaleAttrValueDao;
import com.hannah.hannahmall.product.entity.SkuSaleAttrValueEntity;
import com.hannah.hannahmall.product.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuItemSaleAttrVo> getSaleAttrBySpuId(Long spuId) {
        SkuSaleAttrValueDao baseMapper = this.getBaseMapper();
        List<SkuItemSaleAttrVo> saleAttrVos = baseMapper.getSaleAttrBySpuId(spuId);

        return saleAttrVos;
    }
    @Override
    public List<String> getSkuSaleAttrValuesAsStringList(Long skuId) {

        SkuSaleAttrValueDao baseMapper = this.baseMapper;

        return baseMapper.getSkuSaleAttrValuesAsStringList(skuId);
    }

}