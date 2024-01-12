package com.skillup.domain.promotionCache;

public interface PromotionCacheRepository {
    void setPromotionCache(PromotionCacheDomain promotionCache);

    PromotionCacheDomain getPromotionById(String promotionId);
}
