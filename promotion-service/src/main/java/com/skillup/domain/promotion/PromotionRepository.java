package com.skillup.domain.promotion;

import java.util.List;

public interface PromotionRepository {
    void createPromotion(PromotionDomain promotionDomain);

    PromotionDomain getPromotionById(String promotionId);

    List<PromotionDomain> getPromotionByStatus(int status);

    void updatePromotionStock(PromotionDomain promotionDomain);
}
