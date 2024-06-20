package com.skillup.infrastructure.redis;

import com.alibaba.fastjson.JSON;
import com.skillup.domain.promotionCache.PromotionCacheDomain;
import com.skillup.domain.promotionCache.PromotionCacheRepository;
import com.skillup.domain.stock.StockDomain;
import com.skillup.domain.stock.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Objects;

@Repository
public class RedisRepo implements StockRepository, PromotionCacheRepository {
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    @Qualifier("lockStockScript")
    DefaultRedisScript<Long> redisLockScript;

    @Autowired
    @Qualifier("revertStockScript")
    DefaultRedisScript<Long> redisRevertScript;

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, JSON.toJSONString(value));
    }

    public String get(String key) {
        if (Objects.isNull(key)) return null;
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public boolean lockAvailableStock(StockDomain stockDomain) {
        try {
            Long stock = redisTemplate.execute(redisLockScript,
                    Arrays.asList(
                            StockDomain.createStockKey(stockDomain.getPromotionId()),
                            stockDomain.getOrderId().toString()
                    ), stockDomain.getOperationName().toString());
            if (stock >= 0) {
                return true;
            } else {
                // -1: sold out; -2: promotion doesn't exist
                return false;
            }
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    @Override
    public boolean revertAvailableStock(StockDomain stockDomain) {
        try {
            Long stock = redisTemplate.execute(redisRevertScript,
                    Arrays.asList(
                            StockDomain.createStockKey(stockDomain.getPromotionId()),
                            stockDomain.getOrderId().toString()
                    ), stockDomain.getOperationName().toString());
            if (stock > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    @Override
    public Long getPromotionAvailableStock(String promotionId) {
        String key = StockDomain.createStockKey(promotionId);
        return JSON.parseObject(get(key), Long.class);
    }

    @Override
    public void setPromotionAvailableStock(String promotionId, Long availableStock) {
        String key = StockDomain.createStockKey(promotionId);
        set(key, availableStock);
    }

    @Override
    public void setPromotionCache(PromotionCacheDomain promotionCache) {
        set(promotionCache.getPromotionId(), promotionCache);
    }

    @Override
    public PromotionCacheDomain getPromotionById(String promotionId) {
        return JSON.parseObject(get(promotionId), PromotionCacheDomain.class);
    }
}
