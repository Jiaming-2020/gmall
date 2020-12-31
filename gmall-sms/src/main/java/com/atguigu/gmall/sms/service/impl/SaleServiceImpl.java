package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import com.atguigu.gmall.sms.mapper.SkuBoundsMapper;
import com.atguigu.gmall.sms.service.SaleService;
import com.atguigu.gmall.sms.service.SkuBoundsService;
import com.atguigu.gmall.sms.service.SkuFullReductionService;
import com.atguigu.gmall.sms.service.SkuLadderService;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SaleServiceImpl implements SaleService {
    @Autowired
    private SkuBoundsService skuBoundsService;
    @Autowired
    private SkuLadderService skuLadderService;
    @Autowired
    private SkuFullReductionService skuFullReductionService;

    @Override
    public List<ItemSaleVo> queryItemSaleVosBySkuId(Long skuId) {
        ArrayList<ItemSaleVo> itemSaleVos = new ArrayList<>();

        SkuBoundsEntity skuBoundsEntity = skuBoundsService.getOne(new QueryWrapper<SkuBoundsEntity>().eq("sku_id", skuId));
        if (skuBoundsEntity != null) {
            ItemSaleVo itemSaleVo = new ItemSaleVo();
            itemSaleVo.setType("积分");
            itemSaleVo.setDesc("购物积分：" + skuBoundsEntity.getBuyBounds() + "，成长积分：" + skuBoundsEntity.getGrowBounds());
            itemSaleVos.add(itemSaleVo);
        }

        SkuLadderEntity skuLadderEntity = skuLadderService.getOne(new QueryWrapper<SkuLadderEntity>().eq("sku_id", skuId));
        if (skuLadderEntity != null) {
            ItemSaleVo itemSaleVo = new ItemSaleVo();
            itemSaleVo.setType("打折");
            itemSaleVo.setDesc("满" + skuLadderEntity.getFullCount() + "件，打" + skuLadderEntity.getDiscount() + "折");
            itemSaleVos.add(itemSaleVo);
        }

        SkuFullReductionEntity skuFullReductionEntity = skuFullReductionService.getOne(new QueryWrapper<SkuFullReductionEntity>().eq("sku_id", skuId));
        if (skuFullReductionEntity != null) {
            ItemSaleVo itemSaleVo = new ItemSaleVo();
            itemSaleVo.setType("满减");
            itemSaleVo.setDesc("满" + skuFullReductionEntity.getFullPrice() + "元，减" + skuFullReductionEntity.getReducePrice() + "元");
            itemSaleVos.add(itemSaleVo);
        }
        return itemSaleVos;
    }
}
