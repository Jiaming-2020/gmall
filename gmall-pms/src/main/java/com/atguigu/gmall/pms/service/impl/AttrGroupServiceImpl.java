package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import com.atguigu.gmall.pms.mapper.AttrMapper;
import com.atguigu.gmall.pms.service.AttrService;
import com.atguigu.gmall.pms.service.SkuAttrValueService;
import com.atguigu.gmall.pms.service.SpuAttrValueService;
import com.atguigu.gmall.pms.vo.AttrGroupVO;
import com.atguigu.gmall.pms.vo.AttrValueVo;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.AttrGroupMapper;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.service.AttrGroupService;
import org.springframework.util.CollectionUtils;

@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupMapper, AttrGroupEntity> implements AttrGroupService {
    @Autowired
    private AttrMapper attrMapper;
    @Autowired
    private AttrService attrService;
    @Autowired
    private SkuAttrValueService skuAttrValueService;
    @Autowired
    private SpuAttrValueService spuAttrValueService;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<AttrGroupEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public List<AttrGroupVO> queryAttrGroupVOListByCid(Long catId) {
        return this.list(new QueryWrapper<AttrGroupEntity>().eq("category_id", catId))
                .stream()
                .map(attrGroupEntity -> {
                    AttrGroupVO attrGroupVO = new AttrGroupVO();
                    BeanUtils.copyProperties(attrGroupEntity, attrGroupVO);
                    return attrGroupVO.setAttrEntities(attrMapper.selectList(new QueryWrapper<AttrEntity>().eq("group_id", attrGroupVO.getId()).eq("type", 1)));
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemGroupVo> queryGroupAndAttrAndValueByCidAndSkuIdAndSpuId(Long cid, Long skuId, Long spuId) {
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("category_id", cid));
        if (CollectionUtils.isEmpty(attrGroupEntities)) {
            return null;
        }
        return attrGroupEntities
                .stream()
                .map(attrGroupEntity -> {
                    ItemGroupVo itemGroupVo = new ItemGroupVo();
                    itemGroupVo.setId(attrGroupEntity.getId());
                    itemGroupVo.setGroupName(attrGroupEntity.getName());
                    List<AttrEntity> attrEntities = attrService.list(new QueryWrapper<AttrEntity>().eq("group_id", itemGroupVo.getId()));
                    if (!CollectionUtils.isEmpty(attrEntities)) {
                        ArrayList<AttrValueVo> attrValueVos = new ArrayList<>();
                        List<Long> attrIds = attrEntities.stream().map(AttrEntity::getId).collect(Collectors.toList());
                        List<SkuAttrValueEntity> skuAttrValueEntities = skuAttrValueService.list(new QueryWrapper<SkuAttrValueEntity>().in("attr_id", attrIds).eq("sku_id", skuId));
                        if (!CollectionUtils.isEmpty(skuAttrValueEntities)) {
                            attrValueVos.addAll(
                                    skuAttrValueEntities
                                            .stream()
                                            .map(skuAttrValueEntity -> {
                                                AttrValueVo attrValueVo = new AttrValueVo();
                                                BeanUtils.copyProperties(skuAttrValueEntity, attrValueVo);
                                                return attrValueVo;
                                            })
                                            .collect(Collectors.toList())
                            );
                        }
                        List<SpuAttrValueEntity> spuAttrValueEntities = spuAttrValueService.list(new QueryWrapper<SpuAttrValueEntity>().in("attr_id", attrIds).eq("spu_id", spuId));
                        if (!CollectionUtils.isEmpty(spuAttrValueEntities)) {
                            attrValueVos.addAll(
                                    spuAttrValueEntities
                                            .stream()
                                            .map(spuAttrValueEntity -> {
                                                AttrValueVo attrValueVo = new AttrValueVo();
                                                attrValueVo.setAttrId(spuAttrValueEntity.getAttrId());
                                                attrValueVo.setAttrName(spuAttrValueEntity.getAttrName());
                                                attrValueVo.setAttrValue(spuAttrValueEntity.getAttrValue());
                                                return attrValueVo;
                                            })
                                            .collect(Collectors.toList())
                            );
                        }
                        itemGroupVo.setAttrValues(attrValueVos);
                    }
                    return itemGroupVo;
                })
                .collect(Collectors.toList());
    }

}