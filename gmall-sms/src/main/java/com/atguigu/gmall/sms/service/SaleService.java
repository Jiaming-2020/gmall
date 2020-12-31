package com.atguigu.gmall.sms.service;

import com.atguigu.gmall.sms.vo.ItemSaleVo;

import java.util.List;

public interface SaleService {
    List<ItemSaleVo> queryItemSaleVosBySkuId(Long skuId);
}
