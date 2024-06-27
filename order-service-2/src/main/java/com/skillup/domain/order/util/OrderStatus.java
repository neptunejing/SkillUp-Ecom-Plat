package com.skillup.domain.order.util;

import java.util.HashMap;

public enum OrderStatus {
    ITEM_ERROR(-2), // invalid promotion
    OUT_OF_STOCK(-1), // promotion out of stock
    READY(0), // preorder
    CREATED(1), // waiting to pay
    PAYING(2), // paid
    PAID(3),
    OVERTIME(4); // invalid or expired order

    public final Integer code;

    OrderStatus(Integer code) {
        this.code = code;
    }

    public static final HashMap<Integer, OrderStatus> CACHE = new HashMap<Integer, OrderStatus>() {{
        put(-2, ITEM_ERROR);
        put(-1, OUT_OF_STOCK);
        put(0, READY);
        put(1, CREATED);
        put(2, PAYING);
        put(3, PAID);
        put(4, OVERTIME);
    }};
}
