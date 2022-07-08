package com.hannah.hannahmall.product.app;

import java.util.Arrays;
import java.util.Map;


import com.hannah.hannahmall.common.vliadte.AddGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.hannah.hannahmall.product.entity.BrandEntity;
import com.hannah.hannahmall.product.service.BrandService;
import com.hannah.hannahmall.common.utils.PageUtils;
import com.hannah.hannahmall.common.utils.R;


/**
 * 品牌
 *
 * @author rclin
 * @email rclin@hannah.com
 * @date 2020-06-07 01:12:52
 * 使用JSR303对入参校验
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;


    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = brandService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    // @RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId) {
        BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:brand:save")
    public R save(@Validated(AddGroup.class) @RequestBody BrandEntity brand) {
            brandService.save(brand);
            return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:brand:update")
    public R update(@RequestBody BrandEntity brand) {
        brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:brand:delete")
    public R delete( @RequestBody Long[] brandIds) {
        brandService.delete(Arrays.asList(brandIds));
        return R.ok();
    }

}
