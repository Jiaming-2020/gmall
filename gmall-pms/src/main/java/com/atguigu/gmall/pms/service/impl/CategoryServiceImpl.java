package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.common.util.CacheUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.CategoryMapper;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, CategoryEntity> implements CategoryService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final String CACHE_KEY_PREFIX = "pms:subCategories:";

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<CategoryEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public List<CategoryEntity> querySubCategoriesByPid(Long pid) {
//        return CacheUtils.getDataListFromCache(redisTemplate, CACHE_KEY_PREFIX, pid, CategoryEntity.class, baseMapper, "querySubCategoriesByPid", false);
        return baseMapper.querySubCategoriesByPid(pid);
    }

    @Override
    public List<CategoryEntity> queryCategoriesByParentId(Long parentId) {
        return this.list(parentId == -1 ? null : new QueryWrapper<CategoryEntity>().eq("parent_id", parentId));
    }

    @Override
    public List<CategoryEntity> queryCategoriesByLv3Id(Long id) {
        CategoryEntity categoryEntity3 = this.getById(id);
        CategoryEntity categoryEntity2 = this.getById(categoryEntity3.getParentId());
        CategoryEntity categoryEntity1 = this.getById(categoryEntity2.getParentId());
        return Arrays.asList(categoryEntity1, categoryEntity2, categoryEntity3);
    }

}