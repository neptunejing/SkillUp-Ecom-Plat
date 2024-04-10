package com.skillup.domain.promotionStockLog;

public interface PromotionStockLogRepo {
    void createPromotionStockLog(PromotionStockLogDomain promotionStockLogDomain);

    void updatePromotionStockLog(PromotionStockLogDomain promotionStockLogDomain);

    PromotionStockLogDomain getLogByOrderIdAndOperation(Long orderId, String operationName);
}
