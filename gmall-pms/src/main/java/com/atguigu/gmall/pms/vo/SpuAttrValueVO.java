package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class SpuAttrValueVO extends SpuAttrValueEntity {
    public void setValueSelected(List<String> attrValues) {
        this.setAttrValue(StringUtils.join(attrValues, ","));
    }
}