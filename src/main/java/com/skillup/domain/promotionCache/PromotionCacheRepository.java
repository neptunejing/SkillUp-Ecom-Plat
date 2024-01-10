package com.skillup.domain.promotionCache;

import com.skillup.domain.promotion.PromotionCacheDomain;

public interface PromotionCacheRepository {
    void setPromotionCache(PromotionCacheDomain promotionCache);

    PromotionCacheDomain getPromotionById(String promotionId);
}
