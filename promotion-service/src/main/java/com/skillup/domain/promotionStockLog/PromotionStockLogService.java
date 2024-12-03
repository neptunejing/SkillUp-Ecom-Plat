package com.skillup.domain.promotionStockLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PromotionStockLogService {
    @Autowired
    @Qualifier("batisPromotionLogRepo")
    PromotionStockLogRepo promotionStockLogRepo;

    @Transactional(propagation = Propagation.REQUIRED)
    public PromotionStockLogDomain createPromotionStockLog(PromotionStockLogDomain promotionStockLogDomain) {
        promotionStockLogRepo.createPromotionStockLog(promotionStockLogDomain);
        return promotionStockLogDomain;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public PromotionStockLogDomain updatePromotionStockLog(PromotionStockLogDomain promotionStockLogDomain) {
        promotionStockLogRepo.updatePromotionStockLog(promotionStockLogDomain);
        return promotionStockLogDomain;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public PromotionStockLogDomain getLogByOrderIdAndOperation(Long orderId, String operationName) {
        return promotionStockLogRepo.getLogByOrderIdAndOperation(orderId, operationName);
    }
}
