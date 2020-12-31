package com.atguigu.gmall.pms.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.atguigu.gmall.pms.mapper.AttrMapper;
import com.atguigu.gmall.pms.service.SkuService;
import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import com.atguigu.gmall.pms.vo.SkuVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.service.SkuAttrValueService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("skuAttrValueService")
public class SkuAttrValueServiceImpl extends ServiceImpl<SkuAttrValueMapper, SkuAttrValueEntity> implements SkuAttrValueService {
    @Autowired
    private AttrMapper attrMapper;
    @Autowired
    private SkuService skuService;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuAttrValueEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SkuAttrValueEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bigSave(SkuVO skuVO) {
        List<SkuAttrValueEntity> skuAttrValueEntities = skuVO.getSaleAttrs();
        skuAttrValueEntities.forEach(skuAttrValueEntity ->
                skuAttrValueEntity.setSkuId(skuVO.getId())
        );
        this.saveBatch(skuAttrValueEntities);
    }

    @Override
    public List<SkuAttrValueEntity> querySearchSkuAttrValuesBySkuIdInAttrIds(Long skuId, List<Long> attrIds) {
        return baseMapper.selectList(new QueryWrapper<SkuAttrValueEntity>().eq("sku_id", skuId).in("attr_id", attrIds));
    }

    @Override
    public List<SaleAttrValueVo> querySaleAttrsBySpuId(Long spuId) {
        ArrayList<SaleAttrValueVo> saleAttrValueVos = new ArrayList<>();
        List<SkuEntity> skuEntities = skuService.list(new QueryWrapper<SkuEntity>().eq("spu_id", spuId));
        List<Long> skuIds = skuEntities.stream().map(SkuEntity::getId).collect(Collectors.toList());
        List<SkuAttrValueEntity> skuAttrValueEntities = this.list(new QueryWrapper<SkuAttrValueEntity>().in("sku_id", skuIds));
        if (!CollectionUtils.isEmpty(skuAttrValueEntities)) {
            Map<Long, List<SkuAttrValueEntity>> map = skuAttrValueEntities.stream().collect(Collectors.groupingBy(SkuAttrValueEntity::getAttrId));
            map.forEach((attrId, skuAttrValueList) -> {
                SaleAttrValueVo saleAttrValueVo = new SaleAttrValueVo();
                saleAttrValueVo.setAttrId(attrId);
                saleAttrValueVo.setAttrName(skuAttrValueList.get(0).getAttrName());
                saleAttrValueVo.setAttrValues(skuAttrValueList.stream().map(SkuAttrValueEntity::getAttrValue).collect(Collectors.toSet()));
                saleAttrValueVos.add(saleAttrValueVo);
            });
        }
        return saleAttrValueVos;
    }

    @Override
    public Map<Long, String> querySaleAttrsBySkuId(Long skuId) {
        List<SkuAttrValueEntity> skuAttrValueEntities = this.list(new QueryWrapper<SkuAttrValueEntity>().eq("sku_id", skuId));
        if (CollectionUtils.isEmpty(skuAttrValueEntities)) {
            return null;
        }
        return skuAttrValueEntities.stream().collect(Collectors.toMap(SkuAttrValueEntity::getAttrId, SkuAttrValueEntity::getAttrValue));
    }

    @Override
    public String querySaleAttrValueMappingSkuIdBySpuId(Long spuId) {
        List<SkuEntity> skuEntities = skuService.list(new QueryWrapper<SkuEntity>().eq("spu_id", spuId));
        List<Long> skuIds = skuEntities.stream().map(SkuEntity::getId).collect(Collectors.toList());
        List<Map<String, Object>> maps = baseMapper.querySaleAttrValueMappingSkuIdBySkuIds(skuIds);
        Map<String, Long> mappingMap = maps.stream().collect(Collectors.toMap(map -> (String) map.get("attr_values"), map -> (Long) map.get("sku_id")));
        return JSON.toJSONString(mappingMap);
    }

}