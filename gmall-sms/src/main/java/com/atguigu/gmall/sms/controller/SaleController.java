package com.atguigu.gmall.sms.controller;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.sms.service.SaleService;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("sms/sale")
public class SaleController {
    @Autowired
    private SaleService saleService;

    @GetMapping("sku/{skuId}")
    public ResponseVo<List<ItemSaleVo>> queryItemSaleVosBySkuId(@PathVariable("skuId") Long skuId) {
        return ResponseVo.ok(saleService.queryItemSaleVosBySkuId(skuId));
    }
}
