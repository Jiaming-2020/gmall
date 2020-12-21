package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SpuDescEntity;
import com.atguigu.gmall.pms.entity.SpuEntity;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class SpuVO extends SpuEntity {
    private SpuDescEntity spuDescEntity;
    private List<SpuAttrValueEntity> spuAttrValueEntities;
    private List<SkuVO> skus;

    public void setSpuImages(List<String> spuImages) {
        spuDescEntity = new SpuDescEntity();
        spuDescEntity.setDecript(StringUtils.join(spuImages, ","));
    }

    public void setBaseAttrs(List<SpuAttrValueVO> baseAttrs) {
        spuAttrValueEntities = new ArrayList<>(baseAttrs);
    }
}