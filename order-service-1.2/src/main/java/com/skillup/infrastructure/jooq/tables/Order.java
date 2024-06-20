/*
 * This file is generated by jOOQ.
 */
package com.skillup.infrastructure.jooq.tables;


import com.skillup.infrastructure.jooq.Indexes;
import com.skillup.infrastructure.jooq.Keys;
import com.skillup.infrastructure.jooq.OrderService_1;
import com.skillup.infrastructure.jooq.tables.records.OrderRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row8;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Order extends TableImpl<OrderRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>order-service-1.order</code>
     */
    public static final Order ORDER = new Order();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<OrderRecord> getRecordType() {
        return OrderRecord.class;
    }

    /**
     * The column <code>order-service-1.order.order_number</code>.
     */
    public final TableField<OrderRecord, Long> ORDER_NUMBER = createField(DSL.name("order_number"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>order-service-1.order.order_status</code>.
     */
    public final TableField<OrderRecord, Integer> ORDER_STATUS = createField(DSL.name("order_status"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>order-service-1.order.promotion_id</code>.
     */
    public final TableField<OrderRecord, String> PROMOTION_ID = createField(DSL.name("promotion_id"), SQLDataType.VARCHAR(36).nullable(false), this, "");

    /**
     * The column <code>order-service-1.order.promotion_name</code>.
     */
    public final TableField<OrderRecord, String> PROMOTION_NAME = createField(DSL.name("promotion_name"), SQLDataType.VARCHAR(128).nullable(false), this, "");

    /**
     * The column <code>order-service-1.order.user_id</code>.
     */
    public final TableField<OrderRecord, String> USER_ID = createField(DSL.name("user_id"), SQLDataType.VARCHAR(36).nullable(false), this, "");

    /**
     * The column <code>order-service-1.order.order_amount</code>.
     */
    public final TableField<OrderRecord, Integer> ORDER_AMOUNT = createField(DSL.name("order_amount"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>order-service-1.order.create_time</code>.
     */
    public final TableField<OrderRecord, LocalDateTime> CREATE_TIME = createField(DSL.name("create_time"), SQLDataType.LOCALDATETIME(0).nullable(false), this, "");

    /**
     * The column <code>order-service-1.order.pay_time</code>.
     */
    public final TableField<OrderRecord, LocalDateTime> PAY_TIME = createField(DSL.name("pay_time"), SQLDataType.LOCALDATETIME(0), this, "");

    private Order(Name alias, Table<OrderRecord> aliased) {
        this(alias, aliased, null);
    }

    private Order(Name alias, Table<OrderRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>order-service-1.order</code> table reference
     */
    public Order(String alias) {
        this(DSL.name(alias), ORDER);
    }

    /**
     * Create an aliased <code>order-service-1.order</code> table reference
     */
    public Order(Name alias) {
        this(alias, ORDER);
    }

    /**
     * Create a <code>order-service-1.order</code> table reference
     */
    public Order() {
        this(DSL.name("order"), null);
    }

    public <O extends Record> Order(Table<O> child, ForeignKey<O, OrderRecord> key) {
        super(child, key, ORDER);
    }

    @Override
    public Schema getSchema() {
        return OrderService_1.ORDER_SERVICE_1;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.ORDER_IDX_USER_ID);
    }

    @Override
    public UniqueKey<OrderRecord> getPrimaryKey() {
        return Keys.KEY_ORDER_PRIMARY;
    }

    @Override
    public List<UniqueKey<OrderRecord>> getKeys() {
        return Arrays.<UniqueKey<OrderRecord>>asList(Keys.KEY_ORDER_PRIMARY);
    }

    @Override
    public Order as(String alias) {
        return new Order(DSL.name(alias), this);
    }

    @Override
    public Order as(Name alias) {
        return new Order(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Order rename(String name) {
        return new Order(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Order rename(Name name) {
        return new Order(name, null);
    }

    // -------------------------------------------------------------------------
    // Row8 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row8<Long, Integer, String, String, String, Integer, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row8) super.fieldsRow();
    }
}
