package com.hannah.hannahmall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hannah.hannahmall.common.utils.PageUtils;
import com.hannah.hannahmall.common.utils.Query;

import com.hannah.hannahmall.product.dao.ProductAttrValueDao;
import com.hannah.hannahmall.product.entity.ProductAttrValueEntity;
import com.hannah.hannahmall.product.service.ProductAttrValueService;
import org.springframework.transaction.annotation.Transactional;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveProductAttr(List<ProductAttrValueEntity> collect) {
        this.saveBatch(collect);
    }

    @Override
    public List<ProductAttrValueEntity> baseAttrListforspu(Long spuId) {

        return this.baseMapper.selectList(
                new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));


    }
    /**
     * 修改商品规格
     * @param spuId
     * @param entities
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> entities) {
        //1、删除spuId之前对应的所有属性
        this.baseMapper.delete(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id",spuId));

        //2、添加商品规格信息
        List<ProductAttrValueEntity> collect = entities.stream().peek(item -> item.setSpuId(spuId)).collect(Collectors.toList());
        //批量新增
        this.saveBatch(collect);
    }


}
