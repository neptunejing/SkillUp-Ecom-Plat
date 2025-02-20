package com.skillup.ratelimiter.aspect;

import com.skillup.ratelimiter.annotation.RateLimit;
import com.skillup.ratelimiter.exception.RateLimitException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Aspect
@Component
public class RateLimitAspect {
    private static final String LUA_SCRIPT_PATH = "redis/token_bucket.lua";
    private final RedisTemplate<String, Object> redisTemplate;
    private final DefaultRedisScript<Long> redisScript;

    public RateLimitAspect(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.redisScript = new DefaultRedisScript<>();
        this.redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(LUA_SCRIPT_PATH)));
        this.redisScript.setResultType(Long.class);
    }

    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        // 获取方法的唯一标识(类名 + 方法名)
        String key = joinPoint.getSignature().toShortString();
        int capacity = rateLimit.capacity();
        int rate = rateLimit.rate();
        long now = System.currentTimeMillis() / 1000; // 转换为秒级

        List<String> keys = Collections.singletonList(key);
        Long result = redisTemplate.execute(
                redisScript,
                keys,
                capacity, rate, now
        );

        if (result != null && result == 1) {
            log.info("{} get a token .", key);
            return joinPoint.proceed();
        } else {
            log.error("Too many requests");
            throw new RateLimitException("Too many requests");
        }
    }
}
