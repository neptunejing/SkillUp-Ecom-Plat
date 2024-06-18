package com.skillup.domain.promotionStockLog.util;

public enum OperationStatus {
    ROLLBACK(-1),
    INIT(0),
    CONSUMED(1);

    public final Integer code;

    OperationStatus(Integer code) {
        this.code = code;
    }
}