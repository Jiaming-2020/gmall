package com.atguigu.gmall.search.feign;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.*;
import org.springframework.stereotype.Service;

import java.util.List;

public class PmsFeignFallback implements PmsFeignClient {
    @Override
    public ResponseVo<SpuEntity> querySpuById(Long id) {
        System.out.println("远程调用失败");
        return ResponseVo.fail();
    }

    @Override
    public ResponseVo<List<SpuEntity>> querySpuByPageJson(PageParamVo paramVo) {
        System.out.println("远程调用失败");
        return ResponseVo.fail();
    }

    @Override
    public ResponseVo<List<SkuEntity>> querySkusBySpuId(Long spuId) {
        System.out.println("远程调用失败");
        return ResponseVo.fail();
    }

    @Override
    public ResponseVo<BrandEntity> queryBrandById(Long id) {
        System.out.println("远程调用失败");
        return ResponseVo.fail();
    }

    @Override
    public ResponseVo<CategoryEntity> queryCategoryById(Long id) {
        System.out.println("远程调用失败");
        return ResponseVo.fail();
    }

    @Override
    public ResponseVo<List<SkuAttrValueEntity>> querySearchSkuAttrValuesBySkuIdInAttrIds(Long skuId, List<Long> attrIds) {
        System.out.println("远程调用失败");
        return ResponseVo.fail();
    }

    @Override
    public ResponseVo<List<SpuAttrValueEntity>> querySearchSpuAttrValuesBySpuIdInAttrIds(Long spuId, List<Long> attrIds) {
        System.out.println("远程调用失败");
        return ResponseVo.fail();
    }

    @Override
    public ResponseVo<List<AttrEntity>> querySearchAttrEntitiesByCid(Long cid) {
        System.out.println("远程调用失败");
        return ResponseVo.fail();
    }
}
