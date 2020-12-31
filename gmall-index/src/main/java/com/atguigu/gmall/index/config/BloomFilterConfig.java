package com.atguigu.gmall.index.config;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.index.feign.PmsFeignClient;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class BloomFilterConfig {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private PmsFeignClient pmsFeignClient;

    private static final String KEY_PREFIX = "index:subCategories:";

    @Bean
    public RBloomFilter<String> bloomFilter() {
        RBloomFilter<String> bloomFilter = this.redissonClient.getBloomFilter("index:bloom:filter");
        bloomFilter.tryInit(30, 0.01);
        ResponseVo<List<CategoryEntity>> listResponseVo = this.pmsFeignClient.queryCategoriesByParentId(0L);
        List<CategoryEntity> categoryEntities = listResponseVo.getData();
        bloomFilter.add("index:topCategories" + ":" + Arrays.asList(0L));
        categoryEntities.forEach(categoryEntity -> {
            bloomFilter.add(KEY_PREFIX + Arrays.asList(categoryEntity.getId()));
        });
        return bloomFilter;
    }
}