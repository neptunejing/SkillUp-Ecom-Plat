/*
 * This file is generated by jOOQ.
 */
package com.skillup.infrastructure.jooq.tables.records;


import com.skillup.infrastructure.jooq.tables.Order;

import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Row8;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class OrderRecord extends UpdatableRecordImpl<OrderRecord> implements Record8<Long, Integer, String, String, String, Integer, LocalDateTime, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>order-service-1.order.order_number</code>.
     */
    public void setOrderNumber(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>order-service-1.order.order_number</code>.
     */
    public Long getOrderNumber() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>order-service-1.order.order_status</code>.
     */
    public void setOrderStatus(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>order-service-1.order.order_status</code>.
     */
    public Integer getOrderStatus() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>order-service-1.order.promotion_id</code>.
     */
    public void setPromotionId(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>order-service-1.order.promotion_id</code>.
     */
    public String getPromotionId() {
        return (String) get(2);
    }

    /**
     * Setter for <code>order-service-1.order.promotion_name</code>.
     */
    public void setPromotionName(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>order-service-1.order.promotion_name</code>.
     */
    public String getPromotionName() {
        return (String) get(3);
    }

    /**
     * Setter for <code>order-service-1.order.user_id</code>.
     */
    public void setUserId(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>order-service-1.order.user_id</code>.
     */
    public String getUserId() {
        return (String) get(4);
    }

    /**
     * Setter for <code>order-service-1.order.order_amount</code>.
     */
    public void setOrderAmount(Integer value) {
        set(5, value);
    }

    /**
     * Getter for <code>order-service-1.order.order_amount</code>.
     */
    public Integer getOrderAmount() {
        return (Integer) get(5);
    }

    /**
     * Setter for <code>order-service-1.order.create_time</code>.
     */
    public void setCreateTime(LocalDateTime value) {
        set(6, value);
    }

    /**
     * Getter for <code>order-service-1.order.create_time</code>.
     */
    public LocalDateTime getCreateTime() {
        return (LocalDateTime) get(6);
    }

    /**
     * Setter for <code>order-service-1.order.pay_time</code>.
     */
    public void setPayTime(LocalDateTime value) {
        set(7, value);
    }

    /**
     * Getter for <code>order-service-1.order.pay_time</code>.
     */
    public LocalDateTime getPayTime() {
        return (LocalDateTime) get(7);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record8 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row8<Long, Integer, String, String, String, Integer, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    @Override
    public Row8<Long, Integer, String, String, String, Integer, LocalDateTime, LocalDateTime> valuesRow() {
        return (Row8) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return Order.ORDER.ORDER_NUMBER;
    }

    @Override
    public Field<Integer> field2() {
        return Order.ORDER.ORDER_STATUS;
    }

    @Override
    public Field<String> field3() {
        return Order.ORDER.PROMOTION_ID;
    }

    @Override
    public Field<String> field4() {
        return Order.ORDER.PROMOTION_NAME;
    }

    @Override
    public Field<String> field5() {
        return Order.ORDER.USER_ID;
    }

    @Override
    public Field<Integer> field6() {
        return Order.ORDER.ORDER_AMOUNT;
    }

    @Override
    public Field<LocalDateTime> field7() {
        return Order.ORDER.CREATE_TIME;
    }

    @Override
    public Field<LocalDateTime> field8() {
        return Order.ORDER.PAY_TIME;
    }

    @Override
    public Long component1() {
        return getOrderNumber();
    }

    @Override
    public Integer component2() {
        return getOrderStatus();
    }

    @Override
    public String component3() {
        return getPromotionId();
    }

    @Override
    public String component4() {
        return getPromotionName();
    }

    @Override
    public String component5() {
        return getUserId();
    }

    @Override
    public Integer component6() {
        return getOrderAmount();
    }

    @Override
    public LocalDateTime component7() {
        return getCreateTime();
    }

    @Override
    public LocalDateTime component8() {
        return getPayTime();
    }

    @Override
    public Long value1() {
        return getOrderNumber();
    }

    @Override
    public Integer value2() {
        return getOrderStatus();
    }

    @Override
    public String value3() {
        return getPromotionId();
    }

    @Override
    public String value4() {
        return getPromotionName();
    }

    @Override
    public String value5() {
        return getUserId();
    }

    @Override
    public Integer value6() {
        return getOrderAmount();
    }

    @Override
    public LocalDateTime value7() {
        return getCreateTime();
    }

    @Override
    public LocalDateTime value8() {
        return getPayTime();
    }

    @Override
    public OrderRecord value1(Long value) {
        setOrderNumber(value);
        return this;
    }

    @Override
    public OrderRecord value2(Integer value) {
        setOrderStatus(value);
        return this;
    }

    @Override
    public OrderRecord value3(String value) {
        setPromotionId(value);
        return this;
    }

    @Override
    public OrderRecord value4(String value) {
        setPromotionName(value);
        return this;
    }

    @Override
    public OrderRecord value5(String value) {
        setUserId(value);
        return this;
    }

    @Override
    public OrderRecord value6(Integer value) {
        setOrderAmount(value);
        return this;
    }

    @Override
    public OrderRecord value7(LocalDateTime value) {
        setCreateTime(value);
        return this;
    }

    @Override
    public OrderRecord value8(LocalDateTime value) {
        setPayTime(value);
        return this;
    }

    @Override
    public OrderRecord values(Long value1, Integer value2, String value3, String value4, String value5, Integer value6, LocalDateTime value7, LocalDateTime value8) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached OrderRecord
     */
    public OrderRecord() {
        super(Order.ORDER);
    }

    /**
     * Create a detached, initialised OrderRecord
     */
    public OrderRecord(Long orderNumber, Integer orderStatus, String promotionId, String promotionName, String userId, Integer orderAmount, LocalDateTime createTime, LocalDateTime payTime) {
        super(Order.ORDER);

        setOrderNumber(orderNumber);
        setOrderStatus(orderStatus);
        setPromotionId(promotionId);
        setPromotionName(promotionName);
        setUserId(userId);
        setOrderAmount(orderAmount);
        setCreateTime(createTime);
        setPayTime(payTime);
    }
}
