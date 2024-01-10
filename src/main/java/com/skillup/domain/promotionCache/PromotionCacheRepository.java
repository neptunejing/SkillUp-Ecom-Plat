package com.skillup.domain.promotionCache;

import com.skillup.domain.promotionCache.PromotionCacheDomain;

public interface PromotionCacheRepository {
    void setPromotionCache(PromotionCacheDomain promotionCache);

    PromotionCacheDomain getPromotionById(String promotionId);
}
