package com.atguigu.gmall.index.aspect;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class GmallCacheAspect {
    @Autowired
    private RBloomFilter<String> bloomFilter;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    @Around("@annotation(GmallCache)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        GmallCache gmallCache = method.getAnnotation(GmallCache.class);

        String prefix = gmallCache.prefix();
        List<Object> args = Arrays.asList(joinPoint.getArgs());
        String key = prefix + ":" + args;
        //布隆过滤器
        if (!bloomFilter.contains(key)) {
            return null;
        }

        String cacheJson = redisTemplate.opsForValue().get(key);
        Class<?> returnType = method.getReturnType();
        //缓存
        if (cacheJson != null) {
            return JSON.parseObject(cacheJson, returnType);
        }

        String lock = gmallCache.lock();
        String lockName = lock + ":" + args;
        //加锁
        RLock fairLock = redissonClient.getFairLock(lockName);
        fairLock.lock();

        try {
            //缓存
            String cacheJson2 = redisTemplate.opsForValue().get(key);
            if (cacheJson2 != null) {
                return JSON.parseObject(cacheJson2, returnType);
            }

            //数据库
            Object result = joinPoint.proceed(joinPoint.getArgs());

            //缓存
            if (result != null) {
                int timeout = gmallCache.timeout() + new Random().nextInt(gmallCache.random());
                redisTemplate.opsForValue().set(key, JSON.toJSONString(result), timeout, TimeUnit.MINUTES);
            }
            return result;
        } finally {
            fairLock.unlock();
        }
    }
}
