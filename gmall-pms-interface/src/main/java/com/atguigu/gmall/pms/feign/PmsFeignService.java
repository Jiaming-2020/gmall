package com.atguigu.gmall.pms.feign;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.awt.image.RenderedImage;
import java.util.List;
import java.util.Map;

public interface PmsFeignService {
    @GetMapping("pms/category/parent/{parentId}")
    ResponseVo<List<CategoryEntity>> queryCategoriesByParentId(@PathVariable("parentId") Long parentId);

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

    @GetMapping("pms/category/sub/{pid}")
    ResponseVo<List<CategoryEntity>> querySubCategoriesByPid(@PathVariable("pid") Long pid);

    @GetMapping("pms/sku/{id}")
    ResponseVo<SkuEntity> querySkuById(@PathVariable("id") Long id);

    @GetMapping("pms/category/lv3/{id}")
    ResponseVo<List<CategoryEntity>> queryCategoriesByLv3Id(@PathVariable("id") Long id);

    @GetMapping("pms/skuimages/sku/{skuId}")
    ResponseVo<List<SkuImagesEntity>> querySkuImagesBySkuId(@PathVariable("skuId") Long skuId);

    @GetMapping("pms/skuattrvalue/spu/{spuId}")
    ResponseVo<List<SaleAttrValueVo>> querySaleAttrsBySpuId(@PathVariable("spuId") Long spuId);

    @GetMapping("pms/skuattrvalue/all/sku/{skuId}")
    ResponseVo<Map<Long, String>> querySaleAttrsBySkuId(@PathVariable("skuId") Long skuId);

    @GetMapping("pms/skuattrvalue/mapping/spu/{spuId}")
    ResponseVo<String> querySaleAttrValueMappingSkuIdBySpuId(@PathVariable("spuId") Long spuId);

    @GetMapping("pms/spudesc/{spuId}")
    ResponseVo<SpuDescEntity> querySpuDescById(@PathVariable("spuId") Long spuId);

    @GetMapping("pms/attrgroup/group/attr/value/{cid}")
    ResponseVo<List<ItemGroupVo>> queryGroupAndAttrAndValueByCidAndSkuIdAndSpuId(
            @PathVariable("cid") Long cid,
            @RequestParam("skuId") Long skuId,
            @RequestParam("spuId") Long spuId);
}
