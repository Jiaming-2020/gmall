package com.atguigu.gmall.index.service.impl;

import com.atguigu.gmall.common.util.CacheUtils;
import com.atguigu.gmall.common.util.DistributedLock;
import com.atguigu.gmall.common.util.DistributedLock.MyReentrantLock;
import com.atguigu.gmall.index.aspect.GmallCache;
import com.atguigu.gmall.index.feign.PmsFeignClient;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class IndexServiceImpl implements IndexService {
    @Autowired
    private PmsFeignClient pmsFeignClient;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private DistributedLock distributedLock;
    @Autowired
    private RedissonClient redissonClient;

    private static final String TOP_CACHE_KEY_PREFIX = "index:topCategories";
    private static final String SUB_CACHE_KEY_PREFIX = "index:subCategories";

    @Override
    @GmallCache(prefix = TOP_CACHE_KEY_PREFIX)
    public List<CategoryEntity> queryLv1Categories(Long pid) {
        return pmsFeignClient.queryCategoriesByParentId(pid).getData();
    }

    @Override
    @GmallCache(prefix = SUB_CACHE_KEY_PREFIX)
    public List<CategoryEntity> querySubCategoriesByPid(Long pid) {
        return pmsFeignClient.querySubCategoriesByPid(pid).getData();
    }

    @Override
    public void testLock() {
//        MyReentrantLock lock = distributedLock.getLock("lock");
//        lock.lock();
        RLock lock = redissonClient.getLock("lock");
        lock.lock(30, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set("number", Integer.parseInt(redisTemplate.opsForValue().get("number")) + 1 + "");
//        lock.unlock();
        lock.unlock();
    }
}
