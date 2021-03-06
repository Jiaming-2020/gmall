package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.AttrGroupVO;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author Jiaming
 * @email 849519112@qq.com
 * @date 2020-12-14 19:59:00
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    List<AttrGroupVO> queryAttrGroupVOListByCid(Long catId);

    List<ItemGroupVo> queryGroupAndAttrAndValueByCidAndSkuIdAndSpuId(Long cid, Long skuId, Long spuId);
}

