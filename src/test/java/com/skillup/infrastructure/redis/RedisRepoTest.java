package com.skillup.infrastructure.redis;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisRepoTest {
    @Autowired
    RedisRepo redisRepo;

    public static final String key = "name";
    public static final String value = "skillUp";

    @Test
    public void setAndGetValueTest() {
        redisRepo.set(key, value);
        assertEquals(value, JSON.parseObject(redisRepo.get(key), String.class));
    }
}