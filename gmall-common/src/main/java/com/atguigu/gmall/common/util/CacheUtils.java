package com.atguigu.gmall.common.util;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.bean.ResponseVo;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CacheUtils {
    public static List getDataListFromCache(StringRedisTemplate redisTemplate, String cacheKeyPrefix, Object param, Class dataType, Object object, String methodName, Boolean isRemoteMethod) {
        String cacheJson = redisTemplate.opsForValue().get(cacheKeyPrefix + param);
        if (cacheJson != null) {
            return JSON.parseArray(cacheJson, dataType);
        } else {
            List list = null;
            try {
                Method method = object.getClass().getDeclaredMethod(methodName, param.getClass());
                method.setAccessible(true);
                Object invoke = method.invoke(object, param);
                if (isRemoteMethod) {
                    invoke = ((ResponseVo) invoke).getData();
                }
                list = (List) invoke;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            if (!CollectionUtils.isEmpty(list)) {
                redisTemplate.opsForValue().set(cacheKeyPrefix + param, JSON.toJSONString(list), 30, TimeUnit.DAYS);
            } else {
                redisTemplate.opsForValue().set(cacheKeyPrefix + param, JSON.toJSONString(list), 3, TimeUnit.MINUTES);
            }
            return list;
        }
    }
}