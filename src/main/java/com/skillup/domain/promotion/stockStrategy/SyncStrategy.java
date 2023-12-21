package com.skillup.domain.promotion.stockStrategy;

import com.skillup.domain.promotion.PromotionDomain;
import com.skillup.domain.promotion.PromotionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "sync")
@Slf4j
public class SyncStrategy implements StockOperation {
    @Autowired
    PromotionRepository promotionRepository;

    @Override
    public boolean lockPromotionStock(String promotionId) {
        log.info("----- Sync Strategy -----");
        PromotionDomain promotionDomain = promotionRepository.getPromotionById(promotionId);
        synchronized (this) {
            // 1. Check if out of stock
            if (promotionDomain.getAvailableStock() <= 0) {
                return false;
            }
            log.info("----- Current available stock is: {} -----", promotionDomain.getAvailableStock());
            // 2. Set new values
            promotionDomain.setAvailableStock(promotionDomain.getAvailableStock() - 1);
            promotionDomain.setLockStock(promotionDomain.getLockStock() + 1);
            // 3. Update to database
            promotionRepository.updatePromotionStock(promotionDomain);
            log.info("----- Update available stock to: {} -----", promotionDomain.getAvailableStock());
            return true;
        }
    }
}
