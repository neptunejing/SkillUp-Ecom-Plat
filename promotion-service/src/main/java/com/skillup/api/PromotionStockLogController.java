package com.skillup.api;

import com.skillup.domain.promotionStockLog.PromotionStockLogDomain;
import com.skillup.domain.promotionStockLog.PromotionStockLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/promotionStockLog")
public class PromotionStockLogController {
    @Autowired
    PromotionStockLogService promotionStockLogService;

    @PostMapping()
    public PromotionStockLogDomain createPromotionStockLog(@RequestBody PromotionStockLogDomain promotionStockLogDomain) {
        return promotionStockLogService.createPromotionStockLog(promotionStockLogDomain);
    }

    @PatchMapping()
    public PromotionStockLogDomain updatePromotionStockLog(@RequestBody PromotionStockLogDomain promotionStockLogDomain) {
        return promotionStockLogService.updatePromotionStockLog(promotionStockLogDomain);
    }

    @GetMapping("/id/{id}/op/{op}")
    public PromotionStockLogDomain getPromotionStockLogByIdAndOperation(@PathVariable("id") Long orderId, @PathVariable("op") String operationName) {
        return promotionStockLogService.getLogByOrderIdAndOperation(orderId, operationName);
    }
}
