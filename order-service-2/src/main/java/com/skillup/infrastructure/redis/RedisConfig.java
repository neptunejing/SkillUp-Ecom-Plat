package com.skillup.infrastructure.redis;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {
    @Bean
    RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        // 链接 redis 服务区
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 设置 key 的序列化采用默认的 StringRedisSerializer
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 设置 value 序列化不采用 Jackson2JsonRedisSerializer, 改为一般的 StringRedisSerializer
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
