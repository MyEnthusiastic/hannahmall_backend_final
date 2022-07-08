package com.hannah.hannahmall.product.vo;

import com.hannah.hannahmall.common.feign.vo.SecSessionSkuVO;
import com.hannah.hannahmall.common.feign.vo.SeckillSkuVO;

import com.hannah.hannahmall.product.entity.SkuImagesEntity;
import com.hannah.hannahmall.product.entity.SkuInfoEntity;
import com.hannah.hannahmall.product.entity.SpuInfoDescEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;


/**
 * 商品详情页数据
 */
@ToString
@Data
public class SkuItemVo {

    //1、sku基本信息的获取  pms_sku_info
    private SkuInfoEntity info;

    private boolean hasStock = true;

    //2、sku的图片信息    pms_sku_images
    private List<SkuImagesEntity> images;

    //3、获取spu的销售属性组合
    private List<SkuItemSaleAttrVo> saleAttr;

    //4、获取spu的介绍
    private SpuInfoDescEntity desc;

    //5、获取spu的规格参数信息
    private List<SpuItemAttrGroupVo> groupAttrs;

    //6、查看此商品正在参与的秒杀预告信息
    private SecSessionSkuVO secSessionSkuVO;

}
