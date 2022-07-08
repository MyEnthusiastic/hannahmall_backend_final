package com.hannah.hannahmall.product.app;

import java.util.Arrays;
import java.util.Map;


import com.hannah.hannahmall.product.vo.SpuSaveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.hannah.hannahmall.product.entity.SpuInfoEntity;
import com.hannah.hannahmall.product.service.SpuInfoService;
import com.hannah.hannahmall.common.utils.PageUtils;
import com.hannah.hannahmall.common.utils.R;



/**
 * spu信息
 *
 * @author rclin
 * @email rclin@hannah.com
 * @date 2020-06-07 01:12:52
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;


    /**
     * 商品上架
     * @param spuId
     * @return
     */
    @GetMapping("/{spuId}/up")
    public R up(@PathVariable("spuId") Long spuId){
        spuInfoService.up(spuId);
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("product:spuinfo:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = spuInfoService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
   // @RequiresPermissions("product:spuinfo:info")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("product:spuinfo:save")
    public R save(@RequestBody SpuSaveVo vo){
        spuInfoService.savesupInfo(vo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("product:spuinfo:update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("product:spuinfo:delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
