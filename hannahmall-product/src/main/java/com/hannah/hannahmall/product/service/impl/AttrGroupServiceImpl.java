package com.hannah.hannahmall.product.service.impl;

import com.hannah.hannahmall.common.model.es.Attr;
import com.hannah.hannahmall.product.entity.AttrEntity;
import com.hannah.hannahmall.product.entity.CategoryEntity;
import com.hannah.hannahmall.product.service.AttrService;
import com.hannah.hannahmall.product.service.CategoryService;
import com.hannah.hannahmall.product.vo.AttrGroupWithAttrsVo;
import com.hannah.hannahmall.product.vo.SpuItemAttrGroupItem;
import com.hannah.hannahmall.product.vo.SpuItemAttrGroupVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hannah.hannahmall.common.utils.PageUtils;
import com.hannah.hannahmall.common.utils.Query;

import com.hannah.hannahmall.product.dao.AttrGroupDao;
import com.hannah.hannahmall.product.entity.AttrGroupEntity;
import com.hannah.hannahmall.product.service.AttrGroupService;
import org.springframework.util.StringUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long categoryId) {
        Object key = params.get("key");

        //构造QueryWrapper
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
        if (key != null) {
            wrapper.and((obj) -> {
                obj.eq("attr_group_id", key.toString()).or().like("attr_group_name", key.toString());
            });
        }

        if (categoryId == 0) {
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper
            );
            return new PageUtils(page);
        } else {
            wrapper.eq("catelog_id", categoryId);
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
            return new PageUtils(page);
        }
    }

    /**
     * 根据id查询AttrGroupEntity实体
     *
     * @param attrGroupId
     * @return
     */
    @Override
    public AttrGroupEntity getAttrGroupEntityById(Long attrGroupId) {
        AttrGroupEntity attrGroupEntity = getById(attrGroupId);
        Long catelogId = attrGroupEntity.getCatelogId();
        CategoryEntity categoryEntity = categoryService.getById(catelogId);
        List<Long> list = new ArrayList<>();
        //三级目录在最前面,[734,80,9]; 需要改为[9,80,734];
        List<Long> getcatelogIds = getcatelogIds(list, categoryEntity);
        //将排序反转
        Collections.reverse(getcatelogIds);
        attrGroupEntity.setCatelogIds(getcatelogIds);
        return attrGroupEntity;
    }


    @Override
    public List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {
        //1、查出当前spu对应的所有属性的分组信息以及当前分组下的所有属性对应的值
        AttrGroupDao baseMapper = this.getBaseMapper();
        List<SpuItemAttrGroupItem> items = baseMapper.getAttrGroupWithAttrsBySpuId(spuId, catalogId);
        Map<String, List<SpuItemAttrGroupItem>> map = items.stream().collect(Collectors.groupingBy(SpuItemAttrGroupItem::getAttrGroupName));
        List<SpuItemAttrGroupVo> vos=new ArrayList<>();
        map.forEach((k,v)->{
            SpuItemAttrGroupVo spuItemAttrGroupVo=new SpuItemAttrGroupVo();
            spuItemAttrGroupVo.setGroupName(k);
            List<Attr> attrs = v.stream().map(item -> {
                Attr attr = new Attr();
                attr.setAttrName(item.getAttrName());
                attr.setAttrValue(item.getAttrValue());
                return attr;
            }).collect(Collectors.toList());
            spuItemAttrGroupVo.setAttrs(attrs);
            vos.add(spuItemAttrGroupVo);
        });
        return vos;
    }

    @Override
    public List<AttrGroupEntity> getListByAttrGroupId(Long attrGroupId) {
        List<AttrGroupEntity> list = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("attr_group_id", attrGroupId));
        return list;
    }

    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {

        //1、查询分组信息
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));

        //2、查询所有属性
        List<AttrGroupWithAttrsVo> collect = attrGroupEntities.stream().map(group -> {
            AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(group, attrGroupWithAttrsVo);

            List<AttrEntity> attrs = attrService.getRelationAttr(attrGroupWithAttrsVo.getAttrGroupId());
            attrGroupWithAttrsVo.setAttrs(attrs);

            return attrGroupWithAttrsVo;
        }).collect(Collectors.toList());

        return collect;
    }

    /**
     * 根据entity查找分类id,直到一级分类
     *
     * @param catelogIds
     * @param entity
     * @return
     */
    private List<Long> getcatelogIds(List<Long> catelogIds, CategoryEntity entity) {
        catelogIds.add(entity.getCatId());
        if (entity.getParentCid() != 0) {
            CategoryEntity categoryEntity = categoryService.getById(entity.getParentCid());
            getcatelogIds(catelogIds, categoryEntity);
        }
        return catelogIds;
    }


}
