package com.atguigu.gmall.search.util;

import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.search.feign.PmsFeignClient;
import com.atguigu.gmall.search.feign.WmsFeignClient;
import com.atguigu.gmall.search.pojo.Goods;
import com.atguigu.gmall.search.pojo.SearchAttrValueVO;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QueryUtils {

    public static List<Goods> queryGoodsListBySpuEntity(SpuEntity spuEntity, PmsFeignClient pmsFeignClient, WmsFeignClient wmsFeignClient) {
        ArrayList<Goods> goodsList = new ArrayList<>();
        //获取该spu的品牌信息
        BrandEntity brandEntity = pmsFeignClient.queryBrandById(spuEntity.getBrandId()).getData();
        //获取该spu的分类信息
        CategoryEntity categoryEntity = pmsFeignClient.queryCategoryById(spuEntity.getCategoryId()).getData();
        //获取该spu分类对应的搜索属性集合
        List<AttrEntity> attrEntities = pmsFeignClient.querySearchAttrEntitiesByCid(spuEntity.getCategoryId()).getData();

        //获取该spu的所有SkuEntity集合
        List<SkuEntity> skuEntities = pmsFeignClient.querySkusBySpuId(spuEntity.getId()).getData();
        if (!CollectionUtils.isEmpty(skuEntities)) {
            //遍历SkuEntity集合并转换添加到Goods集合中
            goodsList.addAll(skuEntities.stream().map(skuEntity -> {
                //每个skuEntity对应一个Goods
                Goods goods = new Goods();

                //设置Spu创建时间
                goods.setCreateTime(spuEntity.getCreateTime());

                //设置Sku信息
                goods.setSkuId(skuEntity.getId());
                goods.setDefaultImage(skuEntity.getDefaultImage());
                goods.setPrice(skuEntity.getPrice().doubleValue());
                goods.setTitle(skuEntity.getTitle());
                goods.setSubTitle(skuEntity.getSubtitle());

                //设置库存、销量信息
                List<WareSkuEntity> wareSkuEntities = wmsFeignClient.queryWareSkuListBySkuId(skuEntity.getId()).getData();
                if (!CollectionUtils.isEmpty(wareSkuEntities)) {
                    goods.setSales(wareSkuEntities.stream().map(WareSkuEntity::getSales).reduce((sales1, sales2) -> sales1 + sales2).get());
                    goods.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() - wareSkuEntity.getStockLocked() > 0));
                }

                //设置品牌信息
                if (brandEntity != null) {
                    goods.setBrandId(brandEntity.getId());
                    goods.setBrandName(brandEntity.getName());
                    goods.setLogo(brandEntity.getLogo());
                }

                //设置分类信息
                if (categoryEntity != null) {
                    goods.setCategoryId(categoryEntity.getId());
                    goods.setCategoryName(categoryEntity.getName());
                }

                //设置基本属性
                if (!CollectionUtils.isEmpty(attrEntities)) {
                    List<SpuAttrValueEntity> spuAttrValueEntities = pmsFeignClient.querySearchSpuAttrValuesBySpuIdInAttrIds(
                            spuEntity.getId(),
                            attrEntities.stream()
                                    .filter(attrEntity -> attrEntity.getType() == 1 || attrEntity.getType() == 2)
                                    .map(AttrEntity::getId)
                                    .collect(Collectors.toList())
                    ).getData();
                    if (!CollectionUtils.isEmpty(spuAttrValueEntities)) {
                        goods.setSearchAttrs(spuAttrValueEntities.stream().map(spuAttrValueEntity -> {
                            SearchAttrValueVO searchAttrValueVO = new SearchAttrValueVO();
                            BeanUtils.copyProperties(spuAttrValueEntity, searchAttrValueVO);
                            return searchAttrValueVO;
                        }).collect(Collectors.toList()));
                    }
                }

                //设置销售属性
                if (!CollectionUtils.isEmpty(attrEntities)) {
                    List<SkuAttrValueEntity> skuAttrValueEntities = pmsFeignClient.querySearchSkuAttrValuesBySkuIdInAttrIds(
                            skuEntity.getId(),
                            attrEntities.stream()
                                    .filter(attrEntity -> attrEntity.getType() == 0 || attrEntity.getType() == 2)
                                    .map(AttrEntity::getId)
                                    .collect(Collectors.toList())
                    ).getData();
                    if (!CollectionUtils.isEmpty(skuAttrValueEntities)) {
                        goods.getSearchAttrs().addAll(skuAttrValueEntities.stream().map(skuAttrValueEntity -> {
                            SearchAttrValueVO searchAttrValueVO = new SearchAttrValueVO();
                            BeanUtils.copyProperties(skuAttrValueEntity, searchAttrValueVO);
                            return searchAttrValueVO;
                        }).collect(Collectors.toList()));
                    }
                }

                return goods;
            }).collect(Collectors.toList()));
        }
        return goodsList;
    }
}
