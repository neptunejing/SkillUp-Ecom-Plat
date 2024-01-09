package com.skillup.infrastructure.redis;

import com.alibaba.fastjson.JSON;
import com.skillup.domain.stock.StockDomain;
import com.skillup.domain.stock.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
public class RedisRepo implements StockRepository {
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, JSON.toJSONString(value));
    }

    public String get(String key) {
        if (Objects.isNull(key)) return null;
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public boolean lockAvailableStock(StockDomain stockDomain) {
        return false;
    }

    @Override
    public boolean revertAvailableStock(StockDomain stockDomain) {
        return false;
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
}
