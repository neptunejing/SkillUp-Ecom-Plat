package com.skillup.domain.promotion;

import com.skillup.domain.promotion.stockStrategy.StockOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PromotionService {
    @Autowired
    PromotionRepository promotionRepository;

    @Resource(name = "${promotion.stock-strategy}")
    StockOperation stockOperation;

    public PromotionDomain createPromotion(PromotionDomain promotionDomain) {
        promotionRepository.createPromotion(promotionDomain);
        return promotionDomain;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public PromotionDomain getPromotionById(String promotionId) {
        return promotionRepository.getPromotionById(promotionId);
    }

    public List<PromotionDomain> getPromotionByStatus(int status) {
        return promotionRepository.getPromotionByStatus(status);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean lockPromotionStock(String promotionId) {
        return stockOperation.lockPromotionStock(promotionId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean deductPromotionStock(String promotionId) {
        return stockOperation.deductPromotionStock(promotionId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean revertPromotionStock(String promotionId) {
        return stockOperation.revertPromotionStock(promotionId);
    }
}
