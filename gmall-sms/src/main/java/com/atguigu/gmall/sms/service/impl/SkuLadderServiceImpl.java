package com.atguigu.gmall.sms.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.sms.mapper.SkuLadderMapper;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import com.atguigu.gmall.sms.service.SkuLadderService;
import org.springframework.transaction.annotation.Transactional;


@Service("skuLadderService")
public class SkuLadderServiceImpl extends ServiceImpl<SkuLadderMapper, SkuLadderEntity> implements SkuLadderService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuLadderEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SkuLadderEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bigSave(SkuLadderEntity skuLadder) {
        this.save(skuLadder);
    }

}