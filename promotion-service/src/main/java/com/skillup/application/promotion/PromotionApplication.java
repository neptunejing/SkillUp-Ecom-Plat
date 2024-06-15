package com.skillup.application.promotion;

import com.skillup.application.mapper.PromotionCacheMapper;
import com.skillup.domain.promotionCache.PromotionCacheDomain;
import com.skillup.domain.promotion.PromotionDomain;
import com.skillup.domain.promotion.PromotionService;
import com.skillup.domain.promotionCache.PromotionCacheService;
import com.skillup.domain.stock.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class PromotionApplication {
    @Autowired
    StockService stockService;

    @Autowired
    PromotionCacheService promotionCacheService;

    @Autowired
    PromotionService promotionService;

    public PromotionDomain getPromotionById(String promotionId) {
        // 1. get promotion cache
        PromotionCacheDomain promotionCacheDomain = promotionCacheService.getPromotionById(promotionId);
        if (Objects.isNull(promotionCacheDomain)) {
            // 2. cache miss, get promotion from DB
            log.info("----- PromotionApplication: promotion cache miss -----");
            PromotionDomain promotionDomain = promotionService.getPromotionById(promotionId);
            if (Objects.isNull(promotionDomain)) {
                return null;
            }
            // 3. update cache
            promotionCacheDomain = PromotionCacheMapper.INSTANCE.toCache(promotionDomain);
            promotionCacheService.setPromotionCache(promotionCacheDomain);
        }
        // 4. get stock cache
        Long availableStock = stockService.getAvailableStock(promotionId);
        if (Objects.isNull(availableStock)) {
            return null;
        }
        // 5. update available stock
        promotionCacheDomain.setAvailableStock(availableStock);
        return PromotionCacheMapper.INSTANCE.toDomain(promotionCacheDomain);
    }
}
