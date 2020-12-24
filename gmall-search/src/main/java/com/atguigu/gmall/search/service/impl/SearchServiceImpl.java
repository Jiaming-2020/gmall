package com.atguigu.gmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.search.pojo.Goods;
import com.atguigu.gmall.search.pojo.SearchParamVo;
import com.atguigu.gmall.search.pojo.SearchResponseAttrVo;
import com.atguigu.gmall.search.pojo.SearchResponseVo;
import com.atguigu.gmall.search.service.SearchService;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public SearchResponseVo search(SearchParamVo searchParamVo) {
        try {
            SearchRequest searchRequest = new SearchRequest(new String[]{"goods"}, buildDSL(searchParamVo));
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //设置分页参数并返回
            return parseResult(searchResponse).setPageNum(searchParamVo.getPageNum()).setPageSize(searchParamVo.getPageSize());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private SearchResponseVo parseResult(SearchResponse searchResponse) {
        SearchResponseVo searchResponseVo = new SearchResponseVo();

        if (searchResponse.getAggregations() == null) {
            searchResponseVo.setTotal(1L);
            searchResponseVo.setGoodsList(new ArrayList<>());
            return searchResponseVo;
        }
        //获取聚合映射
        Map<String, Aggregation> aggregationMap = searchResponse.getAggregations().asMap();

        //解析品牌聚合
        List<? extends Terms.Bucket> brandIdAggBuckets = ((ParsedLongTerms) aggregationMap.get("brandIdAgg")).getBuckets();
        if (!CollectionUtils.isEmpty(brandIdAggBuckets)) {
            searchResponseVo.setBrands(
                    brandIdAggBuckets
                            .stream()
                            .map(bucket -> {
                                BrandEntity brandEntity = new BrandEntity();
                                brandEntity.setId(bucket.getKeyAsNumber().longValue());

                                Aggregations subAggregations = bucket.getAggregations();

                                List<? extends Terms.Bucket> brandNameAggBuckets = ((ParsedStringTerms) subAggregations.get("brandNameAgg")).getBuckets();
                                if (!CollectionUtils.isEmpty(brandNameAggBuckets)) {
                                    brandEntity.setName(brandNameAggBuckets.get(0).getKeyAsString());
                                }

                                List<? extends Terms.Bucket> logoAggBuckets = ((ParsedStringTerms) subAggregations.get("logoAgg")).getBuckets();
                                if (!CollectionUtils.isEmpty(logoAggBuckets)) {
                                    brandEntity.setLogo(logoAggBuckets.get(0).getKeyAsString());
                                }

                                return brandEntity;
                            })
                            .collect(Collectors.toList())
            );
        }

        //解析分类聚合
        List<? extends Terms.Bucket> categoryIdAggBuckets = ((ParsedLongTerms) aggregationMap.get("categoryIdAgg")).getBuckets();
        if (!CollectionUtils.isEmpty(categoryIdAggBuckets)) {
            searchResponseVo.setCategories(
                    categoryIdAggBuckets
                            .stream()
                            .map(bucket -> {
                                CategoryEntity categoryEntity = new CategoryEntity();
                                categoryEntity.setId(bucket.getKeyAsNumber().longValue());

                                List<? extends Terms.Bucket> categoryNameAggBuckets = ((ParsedStringTerms) bucket.getAggregations().get("categoryNameAgg")).getBuckets();
                                if (!CollectionUtils.isEmpty(categoryNameAggBuckets)) {
                                    categoryEntity.setName(categoryNameAggBuckets.get(0).getKeyAsString());
                                }

                                return categoryEntity;
                            })
                            .collect(Collectors.toList())
            );
        }

        //解析规格参数聚合
        List<? extends Terms.Bucket> attrIdBuckets = ((ParsedLongTerms) ((ParsedNested) aggregationMap.get("searchAttrsAgg")).getAggregations().get("attrIdAgg")).getBuckets();
        if (!CollectionUtils.isEmpty(attrIdBuckets)) {
            searchResponseVo.setFilters(
                    attrIdBuckets
                            .stream()
                            .map(bucket -> {
                                SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
                                searchResponseAttrVo.setAttrId(bucket.getKeyAsNumber().longValue());

                                List<? extends Terms.Bucket> attrNameAggBuckets = ((ParsedStringTerms) bucket.getAggregations().get("attrNameAgg")).getBuckets();
                                if (!CollectionUtils.isEmpty(attrNameAggBuckets)) {
                                    searchResponseAttrVo.setAttrName(attrNameAggBuckets.get(0).getKeyAsString());
                                }

                                List<? extends Terms.Bucket> attrValueAggBuckets = ((ParsedStringTerms) bucket.getAggregations().get("attrValueAgg")).getBuckets();
                                if (!CollectionUtils.isEmpty(attrValueAggBuckets)) {
                                    searchResponseAttrVo.setAttrValues(
                                            attrValueAggBuckets
                                                    .stream()
                                                    .map(Terms.Bucket::getKeyAsString)
                                                    .collect(Collectors.toList())
                                    );
                                }
                                return searchResponseAttrVo;
                            })
                            .collect(Collectors.toList())
            );
        }

        //解析命中总数
        SearchHits hits = searchResponse.getHits();
        searchResponseVo.setTotal(hits.getTotalHits());

        //解析命中结果集和高亮
        SearchHit[] hitsHits = hits.getHits();
        searchResponseVo.setGoodsList(
                Stream.of(hitsHits)
                        .map(hitsHit -> {
                            Goods goods = JSON.parseObject(hitsHit.getSourceAsString(), Goods.class);
                            goods.setTitle(hitsHit.getHighlightFields().get("title").getFragments()[0].string());
                            return goods;
                        })
                        .collect(Collectors.toList())
        );

        return searchResponseVo;
    }

    private SearchSourceBuilder buildDSL(SearchParamVo searchParamVo) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //查询关键字
        String keyword = searchParamVo.getKeyword();
        if (StringUtils.isBlank(keyword)) {
            return sourceBuilder.query(new MatchQueryBuilder("title", ""));
        }
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(new MatchQueryBuilder("title", keyword).operator(Operator.AND));

        //过滤品牌
        List<Long> brandIds = searchParamVo.getBrandId();
        if (!CollectionUtils.isEmpty(brandIds)) {
            boolQueryBuilder.filter(new TermsQueryBuilder("brandId", brandIds));
        }

        //过滤分类
        List<Long> categoryIds = searchParamVo.getCid();
        if (!CollectionUtils.isEmpty(categoryIds)) {
            boolQueryBuilder.filter(new TermsQueryBuilder("categoryId", categoryIds));
        }

        //过滤规格参数
        List<String> props = searchParamVo.getProps();
        if (!CollectionUtils.isEmpty(props)) {
            props.forEach(prop -> {
                int indexOfColon = prop.indexOf(":");
                if (indexOfColon > 0 && indexOfColon < prop.length() - 1) {
                    String attrId = prop.substring(0, indexOfColon);
                    String[] attrValues = prop.substring(indexOfColon + 1).split("-");
                    boolQueryBuilder.filter(new NestedQueryBuilder("searchAttrs", new BoolQueryBuilder().must(new TermQueryBuilder("searchAttrs.attrId", attrId)).must(new TermsQueryBuilder("searchAttrs.attrValue", attrValues)), ScoreMode.None));
                }
            });
        }

        //过滤库存
        Boolean store = searchParamVo.getStore();
        if (store != null && store) {
            boolQueryBuilder.filter(new TermQueryBuilder("store", true));
        }

        //过滤价格区间
        Double priceFrom = searchParamVo.getPriceFrom();
        Double priceTo = searchParamVo.getPriceTo();
        if (priceFrom != null || priceTo != null) {
            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder("price");
            if (priceFrom != null) {
                rangeQueryBuilder.from(priceFrom);
            }
            if (priceTo != null) {
                rangeQueryBuilder.to(priceTo);
            }
            boolQueryBuilder.filter(rangeQueryBuilder);
        }

        sourceBuilder.query(boolQueryBuilder);

        //排序
        Integer sort = searchParamVo.getSort();
        if (sort != null) {
            switch (sort) {
                case 1:
                    sourceBuilder.sort("price", SortOrder.DESC);
                    break;
                case 2:
                    sourceBuilder.sort("price", SortOrder.ASC);
                    break;
                case 3:
                    sourceBuilder.sort("createTime", SortOrder.DESC);
                    break;
                case 4:
                    sourceBuilder.sort("sales", SortOrder.DESC);
                    break;
            }
        }

        //聚合品牌
        sourceBuilder.aggregation(
                AggregationBuilders.terms("brandIdAgg").field("brandId")
                        .subAggregation(AggregationBuilders.terms("brandNameAgg").field("brandName"))
                        .subAggregation(AggregationBuilders.terms("logoAgg").field("logo"))
        );

        //聚合分类
        sourceBuilder.aggregation(
                AggregationBuilders.terms("categoryIdAgg").field("categoryId")
                        .subAggregation(AggregationBuilders.terms("categoryNameAgg").field("categoryName"))
        );

        //聚合规格参数
        sourceBuilder.aggregation(
                AggregationBuilders.nested("searchAttrsAgg", "searchAttrs")
                        .subAggregation(AggregationBuilders.terms("attrIdAgg").field("searchAttrs.attrId")
                                .subAggregation(AggregationBuilders.terms("attrNameAgg").field("searchAttrs.attrName"))
                                .subAggregation(AggregationBuilders.terms("attrValueAgg").field("searchAttrs.attrValue")))
        );

        //高亮
        sourceBuilder.highlighter(
                new HighlightBuilder()
                        .field("title")
                        .preTags("<font style='color:red'>")
                        .postTags("</font>")
        );

        //分页
        sourceBuilder.from((searchParamVo.getPageNum() - 1) * searchParamVo.getPageSize());
        sourceBuilder.size(searchParamVo.getPageSize());

        return sourceBuilder;
    }
}
