package com.skillup.application.promotion;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "promotion-service", contextId = "promotion")
public interface PromotionServiceApi {
    @PostMapping("/promotion/lock/id/{id}")
    ResponseEntity<Boolean> lockPromotionStock(@PathVariable("id") String promotionId);

    @PostMapping("/promotion/deduct/id/{id}")
    ResponseEntity<Boolean> deductPromotionStock(@PathVariable("id") String promotionId);

    @PostMapping("/promotion/revert/id/{id}")
    ResponseEntity<Boolean> revertPromotionStock(@PathVariable("id") String promotionId);
}
