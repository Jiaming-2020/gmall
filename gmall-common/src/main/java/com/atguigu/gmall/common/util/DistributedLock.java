package com.atguigu.gmall.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class DistributedLock {
    @Autowired
    private StringRedisTemplate redisTemplate;

    public MyReentrantLock getLock(String lockName) {
        return this.getLock(lockName, 30);
    }

    public MyReentrantLock getLock(String lockName, Integer expireSeconds) {
        MyReentrantLock lock = new MyReentrantLock();
        lock.key = lockName;
        lock.expireSeconds = expireSeconds;
        lock.uuid = UUID.randomUUID().toString();
        return lock;
    }

    public class MyReentrantLock {
        private String key;
        private Integer expireSeconds;
        private String uuid;
        private Timer timer;

        private MyReentrantLock() {
        }

        public void lock() {
            String script =
                    "if(redis.call('exists', KEYS[1])==0 or redis.call('hexists', KEYS[1], ARGV[1])==1) " +
                            "then " +
                            "redis.call('hincrby', KEYS[1], ARGV[1], 1) " +
                            "redis.call('expire', KEYS[1], ARGV[2]) " +
                            "return 1 " +
                            "end " +
                            "return 0";
            while (!redisTemplate.execute(new DefaultRedisScript<>(script, Boolean.class), Arrays.asList(this.key), uuid, expireSeconds.toString())) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    redisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS);
                }
            }, expireSeconds * 1000 / 3, expireSeconds * 1000 / 3);
        }

        public void unlock() {
            String script =
                    "if(redis.call('hexists', KEYS[1], ARGV[1])==1) " +
                            "then " +
                            "if(redis.call('hincrby', KEYS[1], ARGV[1], -1)==0) " +
                            "then " +
                            "return redis.call('del', KEYS[1]) " +
                            "end " +
                            "end " +
                            "return 0";
            if (redisTemplate.execute(new DefaultRedisScript<>(script, Boolean.class), Arrays.asList(key), uuid, expireSeconds.toString())) {
                timer.cancel();
            }
        }
    }
}