package com.skillup.domain.promotion.stockStrategy;

public interface StockOperation {
    boolean lockPromotionStock(String promotionId);

    boolean deductPromotionStock(String promotionId);

    boolean revertPromotionStock(String promotionId);
}
