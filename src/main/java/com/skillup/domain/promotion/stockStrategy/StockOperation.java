package com.skillup.domain.promotion.stockStrategy;

public interface StockOperation {
    boolean lockPromotionStock(String promotionId);
}
