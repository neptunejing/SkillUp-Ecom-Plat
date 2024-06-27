package com.skillup.application.promotion;

import com.skillup.domain.stock.StockDomain;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "promotion-service", contextId = "stock")
public interface StockServiceApi {
    @PostMapping("/stock/lock")
    boolean lockAvailableStock(@RequestBody StockDomain stockDomain);

    @PostMapping("/stock/revert")
    boolean revertAvailableStock(@RequestBody StockDomain stockDomain);
}
