package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.item.feign.PmsFeignClient;
import com.atguigu.gmall.item.feign.SmsFeignClient;
import com.atguigu.gmall.item.feign.WmsFeignClient;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.item.vo.ItemVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private PmsFeignClient pmsFeignClient;
    @Autowired
    private SmsFeignClient smsFeignClient;
    @Autowired
    private WmsFeignClient wmsFeignClient;

    @Override
    public ItemVo queryItemVoBySkuId(Long skuId) {
        ItemVo itemVo = new ItemVo();

        //根据skuId查sku
        SkuEntity skuEntity = pmsFeignClient.querySkuById(skuId).getData();
        if (skuEntity == null) {
            return null;
        }
        itemVo.setSkuId(skuEntity.getId());
        itemVo.setTitle(skuEntity.getTitle());
        itemVo.setSubTitle(skuEntity.getSubtitle());
        itemVo.setPrice(skuEntity.getPrice());
        itemVo.setDefaultImage(skuEntity.getDefaultImage());
        itemVo.setWeight(skuEntity.getWeight());

        //根据sku查spu
        SpuEntity spuEntity = pmsFeignClient.querySpuById(skuEntity.getSpuId()).getData();
        itemVo.setSpuId(spuEntity.getId());
        itemVo.setSpuName(spuEntity.getName());

        //根据sku查分类
        List<CategoryEntity> categoryEntities = pmsFeignClient.queryCategoriesByLv3Id(skuEntity.getCatagoryId()).getData();
        itemVo.setCategories(categoryEntities);

        //根据sku查品牌
        BrandEntity brandEntity = pmsFeignClient.queryBrandById(skuEntity.getBrandId()).getData();
        itemVo.setBrandId(brandEntity.getId());
        itemVo.setBrandName(brandEntity.getName());

        //根据skuId查图片
        List<SkuImagesEntity> skuImagesEntities = pmsFeignClient.querySkuImagesBySkuId(skuId).getData();
        itemVo.setImages(skuImagesEntities);

        //根据skuId查营销
        List<ItemSaleVo> itemSaleVos = smsFeignClient.queryItemSaleVosBySkuId(skuId).getData();
        itemVo.setSales(itemSaleVos);

        //根据skuId查库存
        List<WareSkuEntity> wareSkuEntities = wmsFeignClient.queryWareSkuListBySkuId(skuId).getData();
        if (!CollectionUtils.isEmpty(wareSkuEntities)) {
            itemVo.setStore(
                    wareSkuEntities
                            .stream()
                            .anyMatch(wareSkuEntity ->
                                    wareSkuEntity.getStock() - wareSkuEntity.getStockLocked() > 0
                            )
            );
        }

        //根据spu查所有销售属性
        List<SaleAttrValueVo> saleAttrValueVos = pmsFeignClient.querySaleAttrsBySpuId(spuEntity.getId()).getData();
        itemVo.setSaleAttrs(saleAttrValueVos);

        //根据skuId查当前sku的销售属性
        Map<Long, String> map = pmsFeignClient.querySaleAttrsBySkuId(skuId).getData();
        itemVo.setSaleAttr(map);

        //根据spu查映射
        String mapping = pmsFeignClient.querySaleAttrValueMappingSkuIdBySpuId(spuEntity.getId()).getData();
        itemVo.setSkuJsons(mapping);

        //根据spu查商品详情
        SpuDescEntity spuDescEntity = pmsFeignClient.querySpuDescById(spuEntity.getId()).getData();
        if (spuDescEntity != null) {
            itemVo.setSpuImages(Arrays.asList(spuDescEntity.getDescription().split(",")));
        }

        //根据category、skuId、spu查规格参数
        if (!CollectionUtils.isEmpty(categoryEntities) && categoryEntities.size() == 3) {
            List<ItemGroupVo> itemGroupVos = pmsFeignClient.queryGroupAndAttrAndValueByCidAndSkuIdAndSpuId(categoryEntities.get(2).getId(), skuId, spuEntity.getId()).getData();
            itemVo.setGroups(itemGroupVos);
        }

        return itemVo;
    }
}
