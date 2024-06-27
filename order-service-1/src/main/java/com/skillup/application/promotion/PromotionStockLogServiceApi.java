package com.skillup.application.promotion;

import com.skillup.domain.promotionStockLog.PromotionStockLogDomain;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "promotion-service", contextId = "promotionStockLog")
public interface PromotionStockLogServiceApi {
    @PostMapping("/promotionStockLog")
    PromotionStockLogDomain createPromotionStockLog(@RequestBody PromotionStockLogDomain promotionStockLogDomain);

    @PatchMapping("/promotionStockLog")
    PromotionStockLogDomain updatePromotionStockLog(@RequestBody PromotionStockLogDomain promotionStockLogDomain);

    @GetMapping("/promotionStockLog/id/{id}/op/{op}")
    PromotionStockLogDomain getLogByOrderIdAndOperation(@PathVariable("id") Long id, @PathVariable("op") String operationName);
}

