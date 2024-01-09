package com.skillup.domain.stock;

public interface StockRepository {
    boolean lockAvailableStock(StockDomain stockDomain);

    boolean revertAvailableStock(StockDomain stockDomain);

    Long getPromotionAvailableStock(String promotionId);

    void setPromotionAvailableStock(String promotionId, Long availableStock);
}
