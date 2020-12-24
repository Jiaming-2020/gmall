package com.atguigu.gmall.pms.feign;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import org.springframework.stereotype.Service;

@Service
public class SmsFeignFallback implements SmsFeignClient {
    @Override
    public ResponseVo<Object> save(SkuBoundsEntity skuBoundsEntity) {
        System.out.println("远程调用失败");
        return ResponseVo.fail();
    }

    @Override
    public ResponseVo<Object> save(SkuLadderEntity skuLadder) {
        System.out.println("远程调用失败");
        return ResponseVo.fail();
    }

    @Override
    public ResponseVo<Object> save(SkuFullReductionEntity skuFullReduction) {
        System.out.println("远程调用失败");
        return ResponseVo.fail();
    }
}
