package com.atguigu.gmall.wms.feign;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface WmsFeignService {
    @GetMapping("wms/waresku/sku/{skuId}")
    ResponseVo<List<WareSkuEntity>> queryWareSkuListBySkuId(@PathVariable("skuId") Long skuId);
}
