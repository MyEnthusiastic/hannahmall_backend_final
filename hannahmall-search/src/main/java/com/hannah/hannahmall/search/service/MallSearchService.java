package com.hannah.hannahmall.search.service;


import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.hannah.hannahmall.common.feign.ProductServiceAPI;
import com.hannah.hannahmall.common.feign.vo.AttrRespVo;
import com.hannah.hannahmall.common.feign.vo.BrandVo;
import com.hannah.hannahmall.common.feign.vo.CategoryVO;
import com.hannah.hannahmall.common.model.es.SkuEsModel;
import com.hannah.hannahmall.search.constant.EsConstant;
import com.hannah.hannahmall.search.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MallSearchService {
    @Autowired
    RestHighLevelClient restHighLevelClient;


    @Autowired
    private ProductServiceAPI productServiceAPI;

    /**
     * 查询语句见queryDsl.json
     *
     * @param param
     * @return
     * @throws IOException
     */
    public SearchResult search(SearchParam param) throws IOException {
        SearchRequest searchRequest = buildSearchRequest(param);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchResult(search, param);
    }

    /**
     * 得到搜索的返回结果
     *
     * @param search
     * @return
     */
    private SearchResult getSearchResult(SearchResponse search, SearchParam param) {

        SearchResult searchResult = new SearchResult();
        searchResult.setKeyword(param.getKeyword());

        /**
         * 检索结果
         */
        SearchHit[] hits = search.getHits().getHits();
        List<SkuEsModel> collect = Arrays.stream(hits).map(item -> {
            String sourceAsString = item.getSourceAsString();
            SkuEsModel skuEsModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
            //高亮
            HighlightField highlightField = item.getHighlightFields().get("skuTitle");
            if (highlightField != null) {
                Text[] skuTitles = highlightField.getFragments();
                String skuTitle = skuTitles[0].toString();
                skuEsModel.setSkuTitle(skuTitle);
            }
            return skuEsModel;
        }).collect(Collectors.toList());
        /**
         * 分页
         */
        long total = search.getHits().getTotalHits().value;
        Long pageSize = param.getPageSize();//每页大小
        long remainder = total % pageSize;//余数
        long totalPage = remainder > 0 ? total / pageSize + 1 : total / pageSize;//总页码
        searchResult.setTotalPages(totalPage);//总页码
        searchResult.setPageNum(param.getPageNumber());//当前页
        searchResult.setTotal(total);//总记录数
        searchResult.setProduct(collect);
        //聚合结果
        /**
         * 品牌聚合结果
         *  "brandIdAgg" : {
         *       "doc_count_error_upper_bound" : 0,
         *       "sum_other_doc_count" : 0,
         *       "buckets" : [
         *         {
         *           "key" : 4,
         *           "doc_count" : 2,
         *           "brandImgAgg" : {
         *             "doc_count_error_upper_bound" : 0,
         *             "sum_other_doc_count" : 0,
         *             "buckets" : [
         *               {
         *                 "key" : "http://hannahmall.oss-cn-shanghai.aliyuncs.com/2019-07-18/299c55e31d7f50ae4dc85faa90d6f445_121_121.jpg",
         *                 "doc_count" : 2
         *               }
         *             ]
         *           },
         *           "brandNameAgg" : {
         *             "doc_count_error_upper_bound" : 0,
         *             "sum_other_doc_count" : 0,
         *             "buckets" : [
         *               {
         *                 "key" : "京东",
         *                 "doc_count" : 2
         *               }
         *             ]
         *           }
         *         }
         *       ]
         *     },
         */
        Aggregations aggregations = search.getAggregations();
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        Terms brandIdAgg = aggregations.get("brandIdAgg");
        List<? extends Terms.Bucket> brandIdBuckets = brandIdAgg.getBuckets();
        for (Terms.Bucket brandIdBucket : brandIdBuckets) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            //brandId
            long brandId = brandIdBucket.getKeyAsNumber().longValue();//品牌id
            brandVo.setBrandId(brandId);
            //brandImg
            Terms brandImgAgg = brandIdBucket.getAggregations().get("brandImgAgg");
            List<? extends Terms.Bucket> brandImgBuckets = brandImgAgg.getBuckets();
            for (Terms.Bucket brandImgBucket : brandImgBuckets) {
                String brandImg = brandImgBucket.getKeyAsString();
                brandVo.setBrandImg(brandImg);
            }
            //brandName
            Terms brandNameAgg = brandIdBucket.getAggregations().get("brandNameAgg");
            List<? extends Terms.Bucket> brandNameBuckets = brandNameAgg.getBuckets();
            for (Terms.Bucket brandNameBucket : brandNameBuckets) {
                String brandName = brandNameBucket.getKeyAsString();
                brandVo.setBrandName(brandName);
            }
            brandVos.add(brandVo);
        }
        searchResult.setBrands(brandVos);
        /**
         * 分类聚合结果
         * "categoryAgg" : {
         *       "doc_count_error_upper_bound" : 0,
         *       "sum_other_doc_count" : 0,
         *       "buckets" : [
         *         {
         *           "key" : 225,
         *           "doc_count" : 2,
         *           "categoryNameAgg" : {
         *             "doc_count_error_upper_bound" : 0,
         *             "sum_other_doc_count" : 0,
         *             "buckets" : [
         *               {
         *                 "key" : "手机",
         *                 "doc_count" : 2
         *               }
         *             ]
         *           }
         *         }
         *       ]
         *     }
         */
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        Terms categoryAgg = aggregations.get("categoryAgg");
        List<? extends Terms.Bucket> categoryAggBuckets = categoryAgg.getBuckets();
        for (Terms.Bucket categoryAggBucket : categoryAggBuckets) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            long catalogId = categoryAggBucket.getKeyAsNumber().longValue();
            catalogVo.setCatalogId(catalogId);
            Terms categoryNameAgg = categoryAggBucket.getAggregations().get("categoryNameAgg");
            List<? extends Terms.Bucket> categoryNameAggBuckets = categoryNameAgg.getBuckets();
            for (Terms.Bucket categoryNameAggBucket : categoryNameAggBuckets) {
                String catalogName = categoryNameAggBucket.getKeyAsString();
                catalogVo.setCatalogName(catalogName);
            }
            catalogVos.add(catalogVo);
        }
        searchResult.setCatalogs(catalogVos);
        /**
         * 属性聚合
         *  "attrsAgg" : {
         *       "doc_count" : 22,
         *       "attrIdAgg" : {
         *         "doc_count_error_upper_bound" : 0,
         *         "sum_other_doc_count" : 0,
         *         "buckets" : [
         *           {
         *             "key" : 1,
         *             "doc_count" : 6,
         *             "attrNameAgg" : {
         *               "doc_count_error_upper_bound" : 0,
         *               "sum_other_doc_count" : 0,
         *               "buckets" : [
         *                 {
         *                   "key" : "电池容量",
         *                   "doc_count" : 6
         *                 }
         *               ]
         *             },
         *             "attrValueAgg" : {
         *               "doc_count_error_upper_bound" : 0,
         *               "sum_other_doc_count" : 0,
         *               "buckets" : [
         *                 {
         *                   "key" : "3000mAh",
         *                   "doc_count" : 2
         *                 },
         *                 {
         *                   "key" : "4000mAh",
         *                   "doc_count" : 2
         *                 },
         *                 {
         *                   "key" : "5000mAh",
         *                   "doc_count" : 2
         *                 }
         *               ]
         *             }
         *           },
         *           {
         *             "key" : 2,
         *             "doc_count" : 4,
         *             "attrNameAgg" : {
         *               "doc_count_error_upper_bound" : 0,
         *               "sum_other_doc_count" : 0,
         *               "buckets" : [
         *                 {
         *                   "key" : "尺寸",
         *                   "doc_count" : 4
         *                 }
         *               ]
         *             },
         *             "attrValueAgg" : {
         *               "doc_count_error_upper_bound" : 0,
         *               "sum_other_doc_count" : 0,
         *               "buckets" : [
         *                 {
         *                   "key" : "42",
         *                   "doc_count" : 2
         *                 },
         *                 {
         *                   "key" : "55",
         *                   "doc_count" : 2
         *                 }
         *               ]
         *             }
         *           },
         *           {
         *             "key" : 3,
         *             "doc_count" : 4,
         *             "attrNameAgg" : {
         *               "doc_count_error_upper_bound" : 0,
         *               "sum_other_doc_count" : 0,
         *               "buckets" : [
         *                 {
         *                   "key" : "颜色",
         *                   "doc_count" : 4
         *                 }
         *               ]
         *             },
         *             "attrValueAgg" : {
         *               "doc_count_error_upper_bound" : 0,
         *               "sum_other_doc_count" : 0,
         *               "buckets" : [
         *                 {
         *                   "key" : "白色",
         *                   "doc_count" : 2
         *                 },
         *                 {
         *                   "key" : "黑色",
         *                   "doc_count" : 2
         *                 }
         *               ]
         *             }
         *           },
         *           {
         *             "key" : 4,
         *             "doc_count" : 4,
         *             "attrNameAgg" : {
         *               "doc_count_error_upper_bound" : 0,
         *               "sum_other_doc_count" : 0,
         *               "buckets" : [
         *                 {
         *                   "key" : "内存",
         *                   "doc_count" : 4
         *                 }
         *               ]
         *             },
         *             "attrValueAgg" : {
         *               "doc_count_error_upper_bound" : 0,
         *               "sum_other_doc_count" : 0,
         *               "buckets" : [
         *                 {
         *                   "key" : "6g",
         *                   "doc_count" : 2
         *                 },
         *                 {
         *                   "key" : "8g",
         *                   "doc_count" : 2
         *                 }
         *               ]
         *             }
         *           },
         *           {
         *             "key" : 5,
         *             "doc_count" : 4,
         *             "attrNameAgg" : {
         *               "doc_count_error_upper_bound" : 0,
         *               "sum_other_doc_count" : 0,
         *               "buckets" : [
         *                 {
         *                   "key" : "屏幕",
         *                   "doc_count" : 4
         *                 }
         *               ]
         *             },
         *             "attrValueAgg" : {
         *               "doc_count_error_upper_bound" : 0,
         *               "sum_other_doc_count" : 0,
         *               "buckets" : [
         *                 {
         *                   "key" : "4寸",
         *                   "doc_count" : 2
         *                 },
         *                 {
         *                   "key" : "5寸",
         *                   "doc_count" : 2
         *                 }
         *               ]
         *             }
         *           }
         *         ]
         *       }
         *     }
         */
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        Nested attrsAgg = aggregations.get("attrsAgg");
        Terms attrIdAgg = attrsAgg.getAggregations().get("attrIdAgg");
        List<? extends Terms.Bucket> attrIdAggBuckets = attrIdAgg.getBuckets();

        for (Terms.Bucket attrIdAggBucket : attrIdAggBuckets) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            long attrId = attrIdAggBucket.getKeyAsNumber().longValue();
            attrVo.setAttrId(attrId);
            //属性名
            Terms attrNameAgg = attrIdAggBucket.getAggregations().get("attrNameAgg");
            List<? extends Terms.Bucket> attrNameAggBuckets = attrNameAgg.getBuckets();
            for (Terms.Bucket attrNameAggBucket : attrNameAggBuckets) {
                String attrName = attrNameAggBucket.getKeyAsString();
                attrVo.setAttrName(attrName);
            }
            //属性值
            Terms attrValueAgg = attrIdAggBucket.getAggregations().get("attrValueAgg");

            List<String> attrValues = attrValueAgg.getBuckets().stream().map(MultiBucketsAggregation.Bucket::getKeyAsString).collect(Collectors.toList());
            attrVo.setAttrValue(attrValues);
            attrVos.add(attrVo);
        }
        searchResult.setAttrs(attrVos);
        List<String> attrs = param.getAttrs();
        if (attrs != null && attrs.size() > 0) {
            for (String attr : attrs) {
                //attrs=1_5寸^6寸
                String[] split = attr.split("_");
                long attrId = Long.parseLong(split[0]);
                searchResult.getAttrIds().add(attrId);
            }
        }

        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPage; i++) {
            pageNavs.add(i);
        }
        //6、构建面包屑导航
        if (CollectionUtil.isNotEmpty(param.getAttrs())) {
            List<SearchResult.NavVo> navVos = param.getAttrs().stream().map(attr -> {
                //1、分析每一个attrs传过来的参数值
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                String[] s = attr.split("_");
                navVo.setNavValue(s[1]);
                AttrRespVo attrRespVo = productServiceAPI.attrInfo(Long.parseLong(s[0]));
                if (attrRespVo != null) {
                    navVo.setNavName(attrRespVo.getAttrName());
                } else {
                    navVo.setNavName(s[0]);
                }
                //2、取消了这个面包屑以后，我们要跳转到哪个地方，将请求的地址url里面的当前置空
                //拿到所有的查询条件，去掉当前
                String replace = replaceQueryString(param, attr, "attrs");
                navVo.setLink("http://search.hannahmall.com/list.html?" + replace);
                return navVo;
            }).collect(Collectors.toList());
            searchResult.setNavs(navVos);
        }
        // 品牌
        if (CollectionUtil.isNotEmpty(param.getBrandId())) {
            SearchResult.NavVo navVo = new SearchResult.NavVo();
            navVo.setNavName("品牌");
            List<BrandVo> brands = productServiceAPI.brands(param.getBrandId());
            StringBuffer sb = new StringBuffer();
            String replace = "";
            for (BrandVo brand : brands) {
                sb.append(brand.getBrandName());
                replace = replaceQueryString(param, brand.getBrandId().toString(), "brandId");
            }
            navVo.setNavValue(sb.toString());
            navVo.setLink("http://search.hannahmall.com/list.html?" + replace);
            searchResult.getNavs().add(navVo);
        }
        // http://search.hannahmall.com/list.html?keyword=%E5%A4%96%E6%98%9F%E4%BA%BA%E7%AC%94%E8%AE%B0%E6%9C%AC&brandId=13&catalog3Id=451
        if (param.getCatalog3Id() != null) {
            SearchResult.NavVo navVo = new SearchResult.NavVo();
            navVo.setNavName("分类");
            CategoryVO category = productServiceAPI.getByCategory3Id(param.getCatalog3Id());
            navVo.setNavValue(category.getName());
            String replace = param.get_queryString().replace(String.format("&%s=%s", "catalog3Id", param.getCatalog3Id()), "");
            navVo.setLink("http://search.hannahmall.com/list.html?" + replace);
            searchResult.getNavs().add(navVo);
        }

        return searchResult;
    }

    private String replaceQueryString(SearchParam param, String value, String key) {
        String encode;
        try {
            encode = URLEncoder.encode(value, "UTF-8");
            encode.replace("+", "%20");  //浏览器对空格的编码和Java不一样，差异化处理
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return param.get_queryString().replace(String.format("&%s=", key) + value, "");
    }

    private SearchRequest buildSearchRequest(SearchParam param) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(EsConstant.PRODUCT_INDEX);//索引名称
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //查询条件.如果关键字为空则匹配全部
        QueryBuilder queryBuilder = null;
        //如果入参关键字和分类都为空,则
        if (!StringUtils.isEmpty(param.getKeyword())) {
            queryBuilder = QueryBuilders.matchQuery("skuTitle", param.getKeyword());//关键字匹配
            boolQueryBuilder.must(queryBuilder);//查询
        }
        //过滤条件 分类id
        if (param.getCatalog3Id() != null) {
            TermQueryBuilder termQuery = QueryBuilders.termQuery("catalogId", param.getCatalog3Id());
            boolQueryBuilder.filter(termQuery);
        }
        //过滤条件,品牌id 多个
        if (param.getBrandId() != null && param.getBrandId().size() > 0) {
            TermsQueryBuilder termsQuery = QueryBuilders.termsQuery("brandId", param.getBrandId());
            boolQueryBuilder.filter(termsQuery);
        }
        //是否有库存
        Integer hasStock = param.getHasStock();
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("hasStock", hasStock);
        boolQueryBuilder.filter(termQueryBuilder);
        //价格区间 200_500  _500  500_ 三种
        String skuPrice = param.getSkuPrice();
        if (!StringUtils.isEmpty(skuPrice)) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");
            if (skuPrice.startsWith("_")) {//_500
                long endPrice = Long.parseLong(skuPrice.split("_")[1]);
                rangeQueryBuilder.lte(endPrice);
            } else if (skuPrice.endsWith("_")) {//500_
                long startPrice = Long.parseLong(skuPrice.split("_")[0]);
                rangeQueryBuilder.gte(startPrice);
            } else {
                //200_500
                long startPrice = Long.parseLong(skuPrice.split("_")[0]);
                long endPrice = Long.parseLong(skuPrice.split("_")[1]);
                rangeQueryBuilder.gte(startPrice);
                rangeQueryBuilder.lte(endPrice);
            }
            boolQueryBuilder.filter(rangeQueryBuilder);
        }
        //属性筛选 attrs=1_5寸:6寸&attrs=2_安卓:ios
        List<String> attrs = param.getAttrs();
        if (attrs != null && attrs.size() > 0) {
            for (String attr : attrs) {
                //attrs=1_5寸^6寸
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                String[] split = attr.split("_");
                long attrId = Long.parseLong(split[0]);
                String attrValueStr = split[1];
                String[] attrValue = attrValueStr.split(":");
                boolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                boolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValue));
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", boolQuery, ScoreMode.None);
                boolQueryBuilder.filter(nestedQueryBuilder);
            }
        }

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolQueryBuilder);
        /**
         * 页面排序条件
         * saleCount_desc/asc
         * skuPrice_desc/asc
         * hotScore_desc/asc
         */
        String sort = param.getSort();
        if (!StringUtils.isEmpty(sort)) {
            String[] sorts = sort.split("_");
            sourceBuilder.sort(sorts[0], "desc".equals(sorts[1]) ? SortOrder.DESC : SortOrder.ASC);
        }


        /**
         * 分页
         */
        long pageNumber = param.getPageNumber();//当前页码
        long pageSize = param.getPageSize();//每页大小
        //from= (pagenum-1)size
        sourceBuilder.from((int) ((pageNumber - 1) * pageSize));//起始位置
        sourceBuilder.size((int) pageSize);//每页大小
        /**
         *高亮  <b style="color: red">aaa</b>
         */
        if (!StringUtils.isEmpty(param.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.preTags("<b style=color:red>");
            highlightBuilder.postTags("</b>");
            highlightBuilder.field("skuTitle");
            sourceBuilder.highlighter(highlightBuilder);
        }
        /**
         * 按照品牌聚合
         * "brandIdAgg": {
         *       "terms": {
         *         "field": "brandId"
         *       },
         *       "aggs": {
         *         "brandNameAgg": {
         *           "terms": {
         *             "field": "brandName"
         *           }
         *         },
         *         "brandImgAgg": {
         *           "terms": {
         *             "field": "brandImg"
         *           }
         *         }
         *       }
         *     },
         */
        TermsAggregationBuilder brandIdAgg = AggregationBuilders.terms("brandIdAgg").field("brandId").size(10);
        TermsAggregationBuilder brandNameAgg = AggregationBuilders.terms("brandNameAgg").field("brandName");
        TermsAggregationBuilder brandImgAgg = AggregationBuilders.terms("brandImgAgg").field("brandImg");
        brandIdAgg.subAggregation(brandNameAgg);//子聚合
        brandIdAgg.subAggregation(brandImgAgg);//子聚合
        sourceBuilder.aggregation(brandIdAgg);
        /**
         * 分类聚合
         *  "categoryAgg": {
         *       "terms": {
         *         "field": "catalogId"
         *       },
         *       "aggs": {
         *         "categoryNameAgg": {
         *           "terms": {
         *             "field": "catalogName.keyword"
         *           }
         *         }
         *       }
         *     }
         */
        TermsAggregationBuilder catalogIdAgg = AggregationBuilders.terms("categoryAgg").field("catalogId");
        TermsAggregationBuilder categoryNameAgg = AggregationBuilders.terms("categoryNameAgg").field("catalogName.keyword");
        catalogIdAgg.subAggregation(categoryNameAgg);
        sourceBuilder.aggregation(catalogIdAgg);
        /**
         * 属性聚合
         * "attrsAgg": {
         *       "nested": {
         *         "path": "attrs"
         *       },
         *       "aggs": {
         *         "attrIdAgg": {
         *           "terms": {
         *             "field": "attrs.attrId"
         *           },
         *           "aggs": {
         *             "attrNameAgg": {
         *               "terms": {
         *                 "field": "attrs.attrName"
         *               }
         *             },
         *             "attrValueAgg": {
         *               "terms": {
         *                 "field": "attrs.attrValue"
         *               }
         *             }
         *           }
         *         }
         *       }
         *     }
         */
        NestedAggregationBuilder nestedAggregationBuilder = AggregationBuilders.nested("attrsAgg", "attrs");
        //attrs.attrId
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attrIdAgg").field("attrs.attrId");
        // attrs.attrName attrs.attrValue
        TermsAggregationBuilder attrNameAgg = AggregationBuilders.terms("attrNameAgg").field("attrs.attrName");
        TermsAggregationBuilder attrValueAgg = AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue");

        attrIdAgg.subAggregation(attrNameAgg);
        attrIdAgg.subAggregation(attrValueAgg);
        nestedAggregationBuilder.subAggregation(attrIdAgg);
        sourceBuilder.aggregation(nestedAggregationBuilder);
        searchRequest.source(sourceBuilder);
        System.out.println("dsl为:" + sourceBuilder);
        return searchRequest;
    }

    public static void main(String[] args) {
        //价格区间 200_500  _500  500_ 三种
        String s = "200_500";
        String[] s1 = s.split("_");
        System.out.println(s1[0]);
        System.out.println(s1[1]);

    }

}
