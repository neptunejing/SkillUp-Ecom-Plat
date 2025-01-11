package com.skillup.ratelimiter.aspect;

import com.skillup.ratelimiter.annotation.RateLimit;
import com.google.common.util.concurrent.RateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class RateLimitAspect {
    // 存储每个限流注解的 RateLimiter 实例
    private final Map<String, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>();

    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        // 获取方法的唯一标识(类名 + 方法名)
        String key = joinPoint.getSignature().toShortString();

        // 获取或创建 RateLimiter
        RateLimiter rateLimiter = rateLimiterMap.computeIfAbsent(key,
                k -> RateLimiter.create(rateLimit.permitsPerSecond())
        );

        Object result;
        // 阻塞直到拿到令牌
        result = joinPoint.proceed();
        return result;
    }
}
