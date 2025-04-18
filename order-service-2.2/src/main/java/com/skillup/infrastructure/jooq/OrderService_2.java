/*
 * This file is generated by jOOQ.
 */
package com.skillup.infrastructure.jooq;


import com.skillup.infrastructure.jooq.tables.Order;

import java.util.Arrays;
import java.util.List;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class OrderService_2 extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>order-service-2</code>
     */
    public static final OrderService_2 ORDER_SERVICE_2 = new OrderService_2();

    /**
     * The table <code>order-service-2.order</code>.
     */
    public final Order ORDER = Order.ORDER;

    /**
     * No further instances allowed
     */
    private OrderService_2() {
        super("order-service-2", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.<Table<?>>asList(
            Order.ORDER);
    }
}
