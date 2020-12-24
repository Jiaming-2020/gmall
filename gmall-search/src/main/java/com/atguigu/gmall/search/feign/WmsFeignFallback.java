package com.atguigu.gmall.search.feign;

import com.atguigu.gmall.common.bean.ResponseVo;
import entity.WareSkuEntity;
import org.springframework.stereotype.Service;

import java.util.List;

public class WmsFeignFallback implements WmsFeignClient {
    @Override
    public ResponseVo<List<WareSkuEntity>> queryWareSkuListBySkuId(Long skuId) {
        System.out.println("远程调用失败");
        return ResponseVo.fail();
    }
}
