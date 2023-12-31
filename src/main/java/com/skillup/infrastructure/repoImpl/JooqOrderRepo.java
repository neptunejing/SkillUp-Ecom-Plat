package com.skillup.infrastructure.repoImpl;

import com.skillup.domain.order.OrderDomain;
import com.skillup.domain.order.OrderRepository;
import com.skillup.domain.order.util.OrderStatus;
import com.skillup.infrastructure.jooq.tables.Order;
import com.skillup.infrastructure.jooq.tables.records.OrderRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class JooqOrderRepo implements OrderRepository {
    @Autowired
    DSLContext dslContext;

    public static final Order ORDER_T = new Order();


    @Override
    public void createOrder(OrderDomain orderDomain) {
        dslContext.executeInsert(toRecord(orderDomain));
    }

    @Override
    public OrderDomain getOrderById(Long orderId) {
        return dslContext.selectFrom(ORDER_T).where(ORDER_T.ORDER_NUMBER.eq(orderId)).fetchOptional(this::toDomain).orElse(null);
    }


    @Override
    public void updateOrder(OrderDomain orderDomain) {

    }

    private OrderRecord toRecord(OrderDomain orderDomain) {
        OrderRecord orderRecord = new OrderRecord();
        orderRecord.setOrderNumber(orderDomain.getOrderNumber());
        orderRecord.setOrderStatus(orderDomain.getOrderStatus().code);
        orderRecord.setOrderAmount(orderDomain.getOrderAmount());
        orderRecord.setPromotionId(orderDomain.getPromotionId());
        orderRecord.setPromotionName(orderDomain.getPromotionName());
        orderRecord.setUserId(orderDomain.getUserId());
        orderRecord.setCreateTime(orderDomain.getCreateTime());
        orderRecord.setPayTime(orderDomain.getPayTime());
        return orderRecord;
    }

    private OrderDomain toDomain(OrderRecord ordersRecord) {
        return OrderDomain.builder()
                .orderNumber(ordersRecord.getOrderNumber())
                .orderStatus(OrderStatus.CACHE.get(ordersRecord.getOrderStatus()))
                .orderAmount(ordersRecord.getOrderAmount())
                .promotionId(ordersRecord.getPromotionId())
                .promotionName(ordersRecord.getPromotionName())
                .userId(ordersRecord.getUserId())
                .createTime(ordersRecord.getCreateTime())
                .payTime(ordersRecord.getPayTime())
                .build();
    }
}
