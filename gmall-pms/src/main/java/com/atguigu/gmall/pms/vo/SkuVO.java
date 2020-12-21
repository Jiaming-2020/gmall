package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.atguigu.gmall.pms.entity.SkuImagesEntity;
import com.atguigu.gmall.sms.vo.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class SkuVO extends SkuEntity {
    private List<SkuImagesEntity> skuImagesEntities;
    private List<SkuAttrValueEntity> saleAttrs;
    private SkuBoundsEntity skuBoundsEntity = new SkuBoundsEntity();
    private SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
    private SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();

    public void setImages(List<String> images) {
        skuImagesEntities = new ArrayList<>(images.size());
        for (int i = 0; i < images.size(); i++) {
            skuImagesEntities.add(new SkuImagesEntity().setUrl(images.get(i)));
        }
    }

    public void setGrowBounds(BigDecimal growBounds) {
        skuBoundsEntity.setGrowBounds(growBounds);
    }

    public void setBuyBounds(BigDecimal buyBounds) {
        skuBoundsEntity.setBuyBounds(buyBounds);
    }

    public void setWork(List<Integer> work) {
        skuBoundsEntity.setWork(0);
        for (int i = 0; i < work.size(); i++) {
            skuBoundsEntity.setWork(skuBoundsEntity.getWork() + (work.get(i) << i));
        }
    }

    public void setFullCount(Integer fullCount) {
        skuLadderEntity.setFullCount(fullCount);
    }

    public void setDiscount(BigDecimal discount) {
        skuLadderEntity.setDiscount(discount);
    }

    public void setLadderAddOther(Integer ladderAddOther) {
        skuLadderEntity.setAddOther(ladderAddOther);
    }

    public void setFullPrice(BigDecimal fullPrice) {
        skuFullReductionEntity.setFullPrice(fullPrice);
    }

    public void setReducePrice(BigDecimal reducePrice) {
        skuFullReductionEntity.setReducePrice(reducePrice);
    }

    public void setFullAddOther(Integer fullAddOther) {
        skuFullReductionEntity.setAddOther(fullAddOther);
    }
}