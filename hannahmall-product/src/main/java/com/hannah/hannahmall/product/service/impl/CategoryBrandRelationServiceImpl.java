package com.hannah.hannahmall.product.service.impl;

import com.hannah.hannahmall.product.dao.BrandDao;
import com.hannah.hannahmall.product.dao.CategoryDao;
import com.hannah.hannahmall.product.entity.BrandEntity;
import com.hannah.hannahmall.product.entity.CategoryEntity;
import com.hannah.hannahmall.product.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hannah.hannahmall.common.utils.PageUtils;
import com.hannah.hannahmall.common.utils.Query;

import com.hannah.hannahmall.product.dao.CategoryBrandRelationDao;
import com.hannah.hannahmall.product.entity.CategoryBrandRelationEntity;
import com.hannah.hannahmall.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    private CategoryBrandRelationDao categoryBrandRelationDao;

    @Autowired
    private CategoryBrandRelationDao relationDao;

    @Autowired
    private BrandService brandService;
    @Autowired
    private BrandDao brandDao;

    @Autowired
    private CategoryDao categoryDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByBrandId(Map<String, Object> params, Long brandId) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId)
        );

        return new PageUtils(page);
    }

    /**
     * @param brandId
     * @return
     */
    @Override
    public List<CategoryBrandRelationEntity> getListByBrandId(Long brandId) {
        List<CategoryBrandRelationEntity> list = categoryBrandRelationDao.selectList(new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));
        return list;
    }

    @Override
    public List<BrandEntity> getBrandsByCatId(Long catId) {
        List<CategoryBrandRelationEntity> catelogId = relationDao.selectList(new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId));

        List<BrandEntity> collect = catelogId.stream().map(item -> {
            Long brandId = item.getBrandId();
            //查询品牌的详情
            BrandEntity byId = brandService.getById(brandId);
            return byId;
        }).collect(Collectors.toList());

        return collect;
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();

        //1、查询品牌详细信息
        BrandEntity brandEntity = brandDao.selectById(brandId);
        //2、查询分类详细信息
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);

        //将信息保存到categoryBrandRelation中
        categoryBrandRelation.setBrandName(brandEntity.getName());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());
        this.baseMapper.insert(categoryBrandRelation);
    }

    @Override
    public void deleteByBrandIds(List<Long> asList) {
        QueryWrapper<CategoryBrandRelationEntity> qw=new QueryWrapper<>();
        qw.in("brand_id",asList);
        this.remove(qw);
    }

}

