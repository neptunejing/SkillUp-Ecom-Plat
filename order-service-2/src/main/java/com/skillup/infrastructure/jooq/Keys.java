/*
 * This file is generated by jOOQ.
 */
package com.skillup.infrastructure.jooq;


import com.skillup.infrastructure.jooq.tables.Order;
import com.skillup.infrastructure.jooq.tables.records.OrderRecord;

import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables in 
 * order-service-2.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<OrderRecord> KEY_ORDER_PRIMARY = Internal.createUniqueKey(Order.ORDER, DSL.name("KEY_order_PRIMARY"), new TableField[] { Order.ORDER.ORDER_NUMBER }, true);
}
