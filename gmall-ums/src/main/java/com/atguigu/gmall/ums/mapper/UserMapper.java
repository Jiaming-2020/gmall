package com.atguigu.gmall.ums.mapper;

import com.atguigu.gmall.ums.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表
 * 
 * @author Jiaming
 * @email 849519112@qq.com
 * @date 2020-12-15 08:59:26
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
	
}
