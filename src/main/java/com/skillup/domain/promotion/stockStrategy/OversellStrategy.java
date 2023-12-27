package com.skillup.domain.promotion.stockStrategy;

import com.skillup.domain.promotion.PromotionDomain;
import com.skillup.domain.promotion.PromotionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "overall")
@Slf4j
public class OversellStrategy implements StockOperation {
    @Autowired
    PromotionRepository promotionRepository;

    @Override
    public boolean lockPromotionStock(String promotionId) {
        log.info("----- Oversell Strategy -----");
        PromotionDomain promotionDomain = promotionRepository.getPromotionById(promotionId);
        if (promotionDomain.getAvailableStock() <= 0) {
            return false;
        }
        promotionDomain.setAvailableStock(promotionDomain.getAvailableStock() - 1);
        promotionDomain.setLockStock(promotionDomain.getLockStock() + 1);
        promotionRepository.updatePromotionStock(promotionDomain);
        return true;
    }

    @Override
    public boolean deductPromotionStock(String promotionId) {
        return false;
    }

    @Override
    public boolean revertPromotionStock(String promotionId) {
        return false;
    }
}
