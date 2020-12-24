package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.mapper.SpuDescMapper;
import com.atguigu.gmall.pms.service.*;
import com.atguigu.gmall.pms.vo.SkuVO;
import com.atguigu.gmall.pms.vo.SpuAttrValueVO;
import com.atguigu.gmall.pms.vo.SpuVO;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.SpuMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("spuService")
public class SpuServiceImpl extends ServiceImpl<SpuMapper, SpuEntity> implements SpuService {
    @Autowired
    private SpuDescService spuDescService;
    @Autowired
    private SpuAttrValueService spuAttrValueService;
    @Autowired
    private SkuService skuService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SpuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SpuEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public PageResultVo querySpuPageByCategoryId(Long categoryId, PageParamVo pageParamVo) {
        QueryWrapper<SpuEntity> queryWrapper = new QueryWrapper<>();
        if (categoryId != 0) {
            queryWrapper.eq("category_id", categoryId);
        }
        String key = pageParamVo.getKey();
        if (StringUtils.isNotBlank(key)) {
            queryWrapper.and(wrapper -> wrapper.eq("id", key).or().like("name", key));
        }
        return new PageResultVo(this.page(pageParamVo.getPage(), queryWrapper));
    }

    @Override
    @GlobalTransactional
    public void bigSave(SpuVO spuVO) {
        //保存Spu
        this.txSave(spuVO);

        //保存SpuDesc
        spuDescService.bigSave(spuVO);

        //保存SpuAttrValues
        spuAttrValueService.bigSave(spuVO);

        //保存Skus
        skuService.bigSave(spuVO);
//        int i=1/0;
        rabbitTemplate.convertAndSend("PMS_ITEM_EXCHANGE", "item.insert", spuVO.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void txSave(SpuVO spuVO) {
        this.save(spuVO);
    }

    @Override
    public PageResultVo queryOnlinePage(PageParamVo paramVo) {
        IPage<SpuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SpuEntity>().eq("publish_status", 1)
        );

        return new PageResultVo(page);
    }

}