package com.skillup.domain.promotionStockLog.util;

import java.util.HashMap;

public enum OperationStatus {
    ROLLBACK(-1),
    INIT(0),
    CONSUMED(1);

    public final Integer code;

    OperationStatus(Integer code) {
        this.code = code;
    }

    public static final HashMap<Integer, OperationStatus> CACHE = new HashMap<Integer, OperationStatus>() {{
        put(-1, ROLLBACK);
        put(0, INIT);
        put(1, CONSUMED);
    }};
}
