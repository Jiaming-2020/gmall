package com.atguigu.gmall.sms.feign;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface SmsFeignService {
    @PostMapping("sms/skubounds")
    ResponseVo<Object> save(@RequestBody SkuBoundsEntity skuBoundsEntity);

    @PostMapping("sms/skuladder")
    ResponseVo<Object> save(@RequestBody SkuLadderEntity skuLadder);

    @PostMapping("sms/skufullreduction")
    ResponseVo<Object> save(@RequestBody SkuFullReductionEntity skuFullReduction);

    @GetMapping("sms/sale/sku/{skuId}")
    ResponseVo<List<ItemSaleVo>> queryItemSaleVosBySkuId(@PathVariable("skuId") Long skuId);
}
