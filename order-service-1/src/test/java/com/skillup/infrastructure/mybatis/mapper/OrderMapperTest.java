package com.skillup.infrastructure.mybatis.mapper;

import com.skillup.domain.order.OrderDomain;
import com.skillup.domain.order.util.OrderStatus;
import com.skillup.infrastructure.mybatis.Order;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class OrderMapperTest {
    @Autowired
    OrderMapper orderMapper;

    final static long ORDER_NUMBER = 12345678;

    @Test
    void testCreateOrder() {
        OrderDomain orderDomain = OrderDomain.builder()
                .orderNumber(ORDER_NUMBER)
                .orderStatus(OrderStatus.CREATED)
                .promotionId("promo_" + ORDER_NUMBER)
                .promotionName("promo_" + ORDER_NUMBER)
                .userId("user_" + ORDER_NUMBER)
                .orderAmount(1)
                .createTime(LocalDateTime.now())
                .build();
        Order order = toOrder(orderDomain);
        int res = orderMapper.insert(order);

        assert res == 1;
    }

    @Test
    void testGetOrderById() {
        testCreateOrder();

        Order order = orderMapper.selectById(ORDER_NUMBER);

        assert order != null;
        assert order.getOrderNumber().equals(ORDER_NUMBER);
        assert order.getPromotionId().equals("promo_" + ORDER_NUMBER);
        assert order.getPromotionName().equals("promo_" + ORDER_NUMBER);
        assert order.getPromotionId().equals("promo_" + ORDER_NUMBER);
        assert order.getPromotionName().equals("promo_" + ORDER_NUMBER);
    }

    @Test
    void testUpdateOrder() {
        testCreateOrder();

        Order order = orderMapper.selectById(ORDER_NUMBER);
        assert order != null;

        order.setOrderStatus(OrderStatus.PAID.code);
        order.setPayTime(LocalDateTime.now());
        int rowsAffected = orderMapper.updateById(order);

        // 验证更新结果
        assert rowsAffected == 1;

        // 验证更新后的数据
        Order updatedOrder = orderMapper.selectById(ORDER_NUMBER);
        assert updatedOrder != null;
        assert updatedOrder.getOrderStatus().equals(OrderStatus.PAID.code);
        assert updatedOrder.getPayTime() != null;
    }

    @AfterEach
    void cleanUp(@Autowired OrderMapper orderMapper) {
        int rowsDeleted = orderMapper.deleteById(ORDER_NUMBER);
        System.out.println("Deleted rows: " + rowsDeleted);
    }

    private Order toOrder(OrderDomain orderDomain) {
        return Order.builder()
                .orderNumber(orderDomain.getOrderNumber())
                .orderStatus(orderDomain.getOrderStatus().code)
                .orderAmount(orderDomain.getOrderAmount())
                .promotionId(orderDomain.getPromotionId())
                .promotionName(orderDomain.getPromotionName())
                .userId(orderDomain.getUserId())
                .createTime(orderDomain.getCreateTime())
                .payTime(orderDomain.getPayTime())
                .build();

    }

    private OrderDomain toDomain(Order order) {
        return OrderDomain.builder()
                .orderNumber(order.getOrderNumber())
                .orderStatus(OrderStatus.CACHE.get(order.getOrderStatus()))
                .orderAmount(order.getOrderAmount())
                .promotionId(order.getPromotionId())
                .promotionName(order.getPromotionName())
                .userId(order.getUserId())
                .createTime(order.getCreateTime())
                .payTime(order.getPayTime())
                .build();
    }

}
