package com.skillup.domain.promotionCache;

import com.skillup.domain.promotionCache.PromotionCacheDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PromotionCacheService {
    @Autowired
    PromotionCacheRepository promotionCacheRepository;

    public void setPromotionCache(PromotionCacheDomain promotionCache) {
        promotionCacheRepository.setPromotionCache(promotionCache);
    }

    public PromotionCacheDomain getPromotionById(String promotionId) {
        return promotionCacheRepository.getPromotionById(promotionId);
    }
}
