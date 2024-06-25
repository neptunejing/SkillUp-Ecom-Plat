package com.skillup.infrastructure.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRedisRepo {
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    public void addToSet(String key, String value) {
        redisTemplate.opsForSet().add(key, value);
    }

    public boolean getFromSet(String key, String value) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
    }
}
