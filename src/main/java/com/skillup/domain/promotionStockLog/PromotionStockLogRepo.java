package com.skillup.domain.promotionStockLog;

public interface PromotionStockLogRepo {
    void createPromotionStockLog(PromotionStockLogDomain promotionStockLogDomain);

    PromotionStockLogDomain getLogByOrderIdAndOperation(Long orderId, String operationName);
}
