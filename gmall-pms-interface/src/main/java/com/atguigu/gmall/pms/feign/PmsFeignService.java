package com.atguigu.gmall.pms.feign;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface PmsFeignService {
    @GetMapping("pms/spu/{id}")
    ResponseVo<SpuEntity> querySpuById(@PathVariable("id") Long id);

    @PostMapping("pms/spu/page")
    ResponseVo<List<SpuEntity>> querySpuByPageJson(@RequestBody PageParamVo paramVo);

    @GetMapping("pms/sku/spu/{spuId}")
    ResponseVo<List<SkuEntity>> querySkusBySpuId(@PathVariable("spuId") Long spuId);

    @GetMapping("pms/brand/{id}")
    ResponseVo<BrandEntity> queryBrandById(@PathVariable("id") Long id);

    @GetMapping("pms/category/{id}")
    ResponseVo<CategoryEntity> queryCategoryById(@PathVariable("id") Long id);

    @GetMapping("pms/skuattrvalue/sku/{skuId}")
    ResponseVo<List<SkuAttrValueEntity>> querySearchSkuAttrValuesBySkuIdInAttrIds(
            @PathVariable("skuId") Long skuId,
            @RequestParam("attrIds") List<Long> attrIds
    );

    @GetMapping("pms/spuattrvalue/spu/{spuId}")
    ResponseVo<List<SpuAttrValueEntity>> querySearchSpuAttrValuesBySpuIdInAttrIds(
            @PathVariable("spuId") Long spuId,
            @RequestParam("attrIds") List<Long> attrIds
    );

    @GetMapping("pms/attr/ids/{cid}")
    ResponseVo<List<AttrEntity>> querySearchAttrEntitiesByCid(@PathVariable("cid") Long cid);
}
