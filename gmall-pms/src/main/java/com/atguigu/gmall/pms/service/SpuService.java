package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.SpuVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.pms.entity.SpuEntity;

import java.util.Map;

/**
 * spu信息
 *
 * @author Jiaming
 * @email 849519112@qq.com
 * @date 2020-12-14 19:59:00
 */
public interface SpuService extends IService<SpuEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    PageResultVo querySpuPageByCategoryId(Long categoryId, PageParamVo pageParamVo);

    void bigSave(SpuVO spuVO);

    void txSave(SpuVO spuVO);

    PageResultVo queryOnlinePage(PageParamVo paramVo);
}

