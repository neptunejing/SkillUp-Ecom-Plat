package com.skillup.infrastructure.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

@Configuration
public class RedisScriptConfig {
    @Bean(value = "lockStockScript")
    DefaultRedisScript<Long> lockStockScript() {
        // the script returns Long
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("redis/lockStock.lua")));
        redisScript.setResultType(Long.class);
        return redisScript;
    }

    @Bean(value = "revertStockScript")
    DefaultRedisScript<Long> revertStickScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("redis/revertStock.lua")));
        redisScript.setResultType(Long.class);
        return redisScript;
    }
}
