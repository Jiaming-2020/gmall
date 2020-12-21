package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.vo.SkuVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.SkuImagesMapper;
import com.atguigu.gmall.pms.entity.SkuImagesEntity;
import com.atguigu.gmall.pms.service.SkuImagesService;
import org.springframework.transaction.annotation.Transactional;


@Service("skuImagesService")
public class SkuImagesServiceImpl extends ServiceImpl<SkuImagesMapper, SkuImagesEntity> implements SkuImagesService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuImagesEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SkuImagesEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bigSave(SkuVO skuVO) {
        List<SkuImagesEntity> skuImagesEntities = skuVO.getSkuImagesEntities();
        skuImagesEntities.forEach(skuImagesEntity -> {
            skuImagesEntity.setSkuId(skuVO.getId());
            skuImagesEntity.setDefaultStatus(skuImagesEntity.getUrl().equals(skuVO.getDefaultImage()) ? 1 : 0);
        });
        this.saveBatch(skuImagesEntities);
    }

}