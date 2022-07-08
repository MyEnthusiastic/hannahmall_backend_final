package com.hannah.hannahmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hannah.hannahmall.common.constant.ProductConstant;
import com.hannah.hannahmall.product.dao.AttrAttrgroupRelationDao;
import com.hannah.hannahmall.product.dao.AttrGroupDao;
import com.hannah.hannahmall.product.dao.CategoryDao;
import com.hannah.hannahmall.product.entity.AttrAttrgroupRelationEntity;
import com.hannah.hannahmall.product.entity.AttrGroupEntity;
import com.hannah.hannahmall.product.entity.CategoryEntity;
import com.hannah.hannahmall.product.service.AttrAttrgroupRelationService;
import com.hannah.hannahmall.product.service.CategoryService;
import com.hannah.hannahmall.product.vo.AttrGroupRelationVo;
import com.hannah.hannahmall.common.feign.vo.AttrRespVo;
import com.hannah.hannahmall.common.feign.vo.AttrVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hannah.hannahmall.common.utils.PageUtils;
import com.hannah.hannahmall.common.utils.Query;

import com.hannah.hannahmall.product.dao.AttrDao;
import com.hannah.hannahmall.product.entity.AttrEntity;
import com.hannah.hannahmall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {


    @Autowired
    public AttrAttrgroupRelationDao relationDao;
    @Autowired
    private AttrGroupDao attrGroupDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }
    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType) {

        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>()
                .eq("attr_type","base".equalsIgnoreCase(attrType) ?
                        ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());

        //根据catelogId查询信息
        if (catelogId != 0) {
            queryWrapper.eq("catelog_id",catelogId);
        }

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            //attr_id attr_name
            queryWrapper.and((wrapper) -> {
                wrapper.eq("attr_id",key).or().like("attr_name",key);
            });
        }

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );

        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();

        List<AttrRespVo> respVos = records.stream().map((attrEntity) -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);

            //设置分类和分组的名字
            if ("base".equalsIgnoreCase(attrType)) {
                AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attrEntity.getAttrId()));
                if (relationEntity != null && relationEntity.getAttrGroupId() != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                    if (attrGroupEntity!=null){
                        attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                    }

                }
            }
            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                attrRespVo.setCatelogName(categoryEntity.getName());

            }
            return attrRespVo;
        }).collect(Collectors.toList());

        pageUtils.setList(respVos);
        return pageUtils;

    }

    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {

        //1、当前分组只能关联自己所属的分类里面的所有属性
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        //获取当前分类的id
        Long catelogId = attrGroupEntity.getCatelogId();

        //2、当前分组只能关联别的分组没有引用的属性
        //2.1）、当前分类下的其它分组
        List<AttrGroupEntity> groupEntities = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>()
                .eq("catelog_id", catelogId));

        //获取到所有的attrGroupId
        List<Long> collect = groupEntities.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());


        //2.2）、这些分组关联的属性
        List<AttrAttrgroupRelationEntity> groupId = relationDao.selectList
                (new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", collect));

        List<Long> attrIds = groupId.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());

        //2.3）、从当前分类的所有属性移除这些属性
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>()
                .eq("catelog_id", catelogId).eq("attr_type",ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());

        if (attrIds.size() > 0) {
            queryWrapper.notIn("attr_id", attrIds);
        }

        //判断是否有参数进行模糊查询
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and((w) -> {
                w.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);

        return new PageUtils(page);



    }

    @Override
    public List<AttrEntity> getRelationAttr(Long attrGroupId) {
        List<AttrAttrgroupRelationEntity> entities = relationDao.selectList
                (new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrGroupId));

        List<Long> attrIds = entities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());

        if (attrIds.size() == 0) {
            return null;
        }

        return this.baseMapper.selectBatchIds(attrIds);

    }

    @Override
    public void updateAttrById(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);

        this.updateById(attrEntity);

        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            //1、修改分组关联
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attr.getAttrId());

            Integer count = relationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>()
                    .eq("attr_id", attr.getAttrId()));

            if (count > 0) {
                relationDao.update(relationEntity,
                        new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attr.getAttrId()));
            } else {
                relationDao.insert(relationEntity);
            }
        }
    }

    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        //查询详细信息
        AttrEntity attrEntity = this.getById(attrId);

        //查询分组信息
        AttrRespVo respVo = new AttrRespVo();
        BeanUtils.copyProperties(attrEntity,respVo);

        //判断是否是基本类型
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            //1、设置分组信息
            AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne
                    (new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            if (relationEntity != null) {
                respVo.setAttrGroupId(relationEntity.getAttrGroupId());
                //获取分组名称
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                if (attrGroupEntity != null) {
                    respVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }

        //2、设置分类信息
        Long catelogId = attrEntity.getCatelogId();
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);

        respVo.setCatelogPath(catelogPath);
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        if (categoryEntity != null) {
            respVo.setCatelogName(categoryEntity.getName());
        }
        return respVo;
    }

    @Override
    public void deleteRelation(AttrGroupRelationVo[] vos) {
        List<AttrAttrgroupRelationEntity> entities = Arrays.stream(vos).map((item) -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        relationDao.deleteBatchRelation(entities);
    }

    @Override
    @Transactional
    public void removeRelationByIds(List<Long> asList) {
        this.removeByIds(asList);
        QueryWrapper<AttrAttrgroupRelationEntity>qw=new QueryWrapper<AttrAttrgroupRelationEntity>()
                .in("attr_id",asList);
        attrAttrgroupRelationService.remove(qw);
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);

        this.save(attrEntity);

        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId() != null) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attrEntity.getAttrId());
            relationDao.insert(relationEntity);
        }

    }
}
