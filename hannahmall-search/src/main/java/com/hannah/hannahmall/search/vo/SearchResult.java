package com.hannah.hannahmall.search.vo;

import com.hannah.hannahmall.common.model.es.SkuEsModel;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索结果
 */
@Data
@ToString
public class SearchResult {

    /**
     * keywords
     */
    private String keyword;
    /**
     * 查询的商品
     */
    private List<SkuEsModel> product;
    /**
     * 当前页
     */
    private Long pageNum;
    /**
     * 总页码
     */
    private Long totalPages;


    /**
     * 总记录数
     */
    private Long total;


    private List<Integer> pageNavs;


    /**
     * 品牌 资源聚合
     */
    private List<BrandVo> brands;
    /**
     * 分类 资源聚合
     */
    private List<CatalogVo> catalogs;

    /**
     * 属性
     */
    private List<AttrVo> attrs;

    /**
     * 入参属性Id集合
     */
    private List<Long> attrIds=new ArrayList<>();





    /* 面包屑导航数据 */
    private List<NavVo> navs=new ArrayList<>();

    @Data
    public static class NavVo {
        private String navName;
        private String navValue;
        private String link;
    }



    @Data
    public static class AttrVo {

        private Long attrId;

        private String attrName;

        private List<String> attrValue;
    }


    @Data
    public static class CatalogVo {

        private Long catalogId;

        private String catalogName;
    }

    @Data
    public static class BrandVo {

        private Long brandId;

        private String brandName;

        private String brandImg;
    }



}
