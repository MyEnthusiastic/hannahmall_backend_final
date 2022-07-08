package com.hannah.hannahmall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


import com.hannah.hannahmall.product.entity.AttrEntity;
import com.hannah.hannahmall.product.service.AttrAttrgroupRelationService;
import com.hannah.hannahmall.product.service.AttrService;
import com.hannah.hannahmall.product.vo.AttrGroupRelationVo;
import com.hannah.hannahmall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.hannah.hannahmall.product.entity.AttrGroupEntity;
import com.hannah.hannahmall.product.service.AttrGroupService;
import com.hannah.hannahmall.common.utils.PageUtils;
import com.hannah.hannahmall.common.utils.R;



/**
 * 属性分组
 *
 * @author rclin
 * @email rclin@hannah.com
 * @date 2020-06-07 01:12:52
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private AttrService attrService;
    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    /**
     * 列表
     */
    @RequestMapping("/list/{categoryId}")
    public R list(@RequestParam Map<String, Object> params,@PathVariable("categoryId")Long categoryId){
        PageUtils page = attrGroupService.queryPage(params,categoryId);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getAttrGroupEntityById(attrGroupId);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }


    /**
     * 根据attrGroupId查询属性分组id
     * @param attrGroupId
     * @return
     */
    @RequestMapping("/{attrGroupId}/attr/relation")
    // @RequiresPermissions("product:attrgroup:delete")
    public R getAttr(@PathVariable("attrGroupId") Long attrGroupId){
        List<AttrEntity> entities = attrService.getRelationAttr(attrGroupId);

        return R.ok().put("data",entities);
    }


    /**
     * 获取属性分组没有关联的其他属性
     */
    @GetMapping(value = "/{attrgroupId}/noattr/relation")
    public R attrNoattrRelation(@RequestParam Map<String, Object> params,
                                @PathVariable("attrgroupId") Long attrgroupId) {

        // List<AttrEntity> entities = attrService.getRelationAttr(attrgroupId);

        PageUtils page = attrService.getNoRelationAttr(params,attrgroupId);

        return R.ok().put("page",page);
    }


    @GetMapping(value = "/{catelogId}/withattr")
    public R getAttrGroupWithAttrs(@PathVariable("catelogId") Long catelogId) {

        //1、查出当前分类下的所有属性分组
        //2、查出每个属性分组下的所有属性
        List<AttrGroupWithAttrsVo> vos = attrGroupService.getAttrGroupWithAttrsByCatelogId(catelogId);


        return R.ok().put("data",vos);

    }

    @PostMapping(value = "/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrGroupRelationVo[] vos) {

        attrService.deleteRelation(vos);

        return R.ok();
    }

    @PostMapping(value = "/attr/relation")
    public R addRelation(@RequestBody List<AttrGroupRelationVo> vos) {

        attrAttrgroupRelationService.saveBatch(vos);

        return R.ok();

    }

}
