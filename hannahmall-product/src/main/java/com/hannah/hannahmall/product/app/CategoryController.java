package com.hannah.hannahmall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.hannah.hannahmall.product.entity.CategoryEntity;
import com.hannah.hannahmall.product.service.CategoryService;
import com.hannah.hannahmall.common.utils.R;



/**
 * 商品三级分类
 *
 * @author rclin
 * @email rclin@hannah.com
 * @date 2020-06-07 01:12:52
 */
@RestController
@RequestMapping("product/category")
@Slf4j(topic = "CategoryController")
@Api(value = "商品分类模块", tags = "商品分类模块")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 列表tree
     */
    @GetMapping("/list/tree")
    @ApiOperation(value = "分类列表")
    public R list(@RequestParam Map<String, Object> params){
        List<CategoryEntity>list = categoryService.listWithTree();
        return R.ok().put("data", list);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
   // @RequiresPermissions("product:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);
        return R.ok().put("data", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.saveAndRefreshCache(category);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryEntity category){
		categoryService.updateByIdAndRefreshCache(category);
        return R.ok();
    }

    /**
     * 修改顺序
     */
    @RequestMapping("/update/sort")
    public R updateSort(@RequestBody CategoryEntity[] category){
        categoryService.updateByIds(Arrays.asList(category));
        return R.ok();
    }



    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] catIds){
		//categoryService.removeByIds(Arrays.asList(catIds));
        categoryService.removeMenuByIds(Arrays.asList(catIds));
        return R.ok();
    }

}
