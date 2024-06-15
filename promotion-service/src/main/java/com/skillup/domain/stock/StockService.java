package com.skillup.domain.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockService {
    @Autowired
    StockRepository stockRepository;

    public boolean lockAvailableStock(StockDomain stockDomain) {
        return stockRepository.lockAvailableStock(stockDomain);
    }

    public boolean revertAvailableStock(StockDomain stockDomain) {
        return stockRepository.revertAvailableStock(stockDomain);
    }

    public Long getAvailableStock(String promotionId) {
        return stockRepository.getPromotionAvailableStock(promotionId);
    }

    public void setAvailableStock(String promotionId, long availablestock) {
        stockRepository.setPromotionAvailableStock(promotionId, availablestock);
    }
}
