package com.atguigu.gmall.sms.feign;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.sms.vo.SkuBoundsEntity;
import com.atguigu.gmall.sms.vo.SkuFullReductionEntity;
import com.atguigu.gmall.sms.vo.SkuLadderEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface SmsFeignService {
    @PostMapping("sms/skubounds")
    ResponseVo<Object> save(@RequestBody SkuBoundsEntity skuBoundsEntity);

    @PostMapping("sms/skuladder")
    ResponseVo<Object> save(@RequestBody SkuLadderEntity skuLadder);

    @PostMapping("sms/skufullreduction")
    ResponseVo<Object> save(@RequestBody SkuFullReductionEntity skuFullReduction);
}
