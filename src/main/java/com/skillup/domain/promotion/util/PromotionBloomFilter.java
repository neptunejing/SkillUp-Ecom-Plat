package com.skillup.domain.promotion.util;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PromotionBloomFilter {

    private final RBloomFilter<String> bloomFilter;

    @Autowired
    public PromotionBloomFilter(RedissonClient redissonClient) {
        bloomFilter = redissonClient.getBloomFilter("promotionBloomFilter");
        // 预计插入元素数量和期望误差率
        bloomFilter.tryInit(1000, 0.03);
    }

    // 在需要的地方使用布隆过滤器
    public boolean mightContain(String value) {
        return bloomFilter.contains(value);
    }

    public void addValue(String value) {
        bloomFilter.add(value);
    }
}
