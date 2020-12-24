package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.feign.SmsFeignClient;
import com.atguigu.gmall.pms.service.SkuAttrValueService;
import com.atguigu.gmall.pms.service.SkuImagesService;
import com.atguigu.gmall.pms.vo.SkuVO;
import com.atguigu.gmall.pms.vo.SpuVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.SkuMapper;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.atguigu.gmall.pms.service.SkuService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("skuService")
public class SkuServiceImpl extends ServiceImpl<SkuMapper, SkuEntity> implements SkuService {
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SkuAttrValueService skuAttrValueService;
    @Autowired
    private SmsFeignClient smsFeignClient;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SkuEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bigSave(SpuVO spuVO) {
        List<SkuVO> skus = spuVO.getSkus();
        skus.forEach(skuVO -> {
            //保存Sku
            skuVO.setSpuId(spuVO.getId());
            skuVO.setCatagoryId(spuVO.getCategoryId());
            skuVO.setBrandId(spuVO.getBrandId());
            if (!StringUtils.isNotBlank(skuVO.getDefaultImage())) {
                if (!CollectionUtils.isEmpty(skuVO.getSkuImagesEntities())) {
                    skuVO.setDefaultImage(skuVO.getSkuImagesEntities().get(0).getUrl());
                }
            }
            this.save(skuVO);

            //保存SkuImages
            skuImagesService.bigSave(skuVO);

            //保存SkuAttrValues
            skuAttrValueService.bigSave(skuVO);

            //保存SkuBounds
            smsFeignClient.save(skuVO.getSkuBoundsEntity().setSkuId(skuVO.getId()));

            //保存SkuLadder
            smsFeignClient.save(skuVO.getSkuLadderEntity().setSkuId(skuVO.getId()));

            //保存SkuFullReduction
            smsFeignClient.save(skuVO.getSkuFullReductionEntity().setSkuId(skuVO.getId()));
        });
    }

}