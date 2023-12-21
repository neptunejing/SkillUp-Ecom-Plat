package com.skillup.domain.promotion;

import com.skillup.domain.promotion.stockStrategy.StockOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public PromotionDomain getPromotionById(String promotionId) {
        return promotionRepository.getPromotionById(promotionId);
    }

    public List<PromotionDomain> getPromotionByStatus(int status) {
        return promotionRepository.getPromotionByStatus(status);
    }

    public boolean lockPromotionStock(String promotionId) {
        return stockOperation.lockPromotionStock(promotionId);
    }

    public boolean deductPromotionStock(String promotionId) {
        return true;
    }

    public boolean revertPromotionStock(String promotionId) {
        return true;
    }
}
