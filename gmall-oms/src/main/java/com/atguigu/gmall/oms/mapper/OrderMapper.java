package com.atguigu.gmall.oms.mapper;

import com.atguigu.gmall.oms.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author Jiaming
 * @email 849519112@qq.com
 * @date 2020-12-15 09:05:18
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity> {
	
}
