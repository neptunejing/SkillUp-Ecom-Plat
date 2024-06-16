package com.skillup.domain.stock;

import com.skillup.domain.promotionStockLog.util.OperationName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockDomain {
    private String promotionId;

    private String availableStock;

    private Long orderId;

    private OperationName operationName;

    public static final String PROMOTION_PREFIX = "PROMOTION_";

    public static final String STOCK_SUFFIX = "_STOCK";

    public static String createStockKey(String promotionId) {
        return PROMOTION_PREFIX + promotionId + STOCK_SUFFIX;
    }
}
