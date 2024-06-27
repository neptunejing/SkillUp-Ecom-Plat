package com.skillup.api;

import com.skillup.domain.stock.StockDomain;
import com.skillup.domain.stock.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stock")
public class StockController {
    @Autowired
    StockService stockService;

    @PostMapping("/lock")
    public boolean lockAvailableStock(@RequestBody StockDomain stockDomain) {
        return stockService.lockAvailableStock(stockDomain);
    }

    @PostMapping("/revert")
    public boolean revertAvailableStock(@RequestBody StockDomain stockDomain) {
        return stockService.revertAvailableStock(stockDomain);
    }
}
