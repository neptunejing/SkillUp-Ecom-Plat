package com.skillup.application.promotion;

import com.skillup.domain.promotion.PromotionDomain;
import com.skillup.domain.promotion.PromotionService;
import com.skillup.domain.stock.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PromotionPreheatApplication implements ApplicationRunner {
    @Autowired
    StockService stockService;
    @Autowired
    PromotionService promotionService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("----- Promotion Preheat: set available stock to cache -----");
        // get all active promotions
        List<PromotionDomain> promotionDomainList = promotionService.getPromotionByStatus(1);
        promotionDomainList.forEach(promotionDomain -> {
            // set available stocks to cache
            stockService.setAvailableStock(promotionDomain.getPromotionId(), promotionDomain.getAvailableStock());
        });
    }
}
