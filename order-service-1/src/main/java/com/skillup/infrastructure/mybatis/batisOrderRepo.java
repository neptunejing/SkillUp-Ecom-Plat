package com.skillup.infrastructure.mybatis;

import com.skillup.domain.order.OrderDomain;
import com.skillup.domain.order.OrderRepository;
import com.skillup.domain.order.util.OrderStatus;
import com.skillup.infrastructure.mybatis.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Repository
public class batisOrderRepo implements OrderRepository {
    @Autowired
    OrderMapper orderMapper;

    @Override
    @Transactional
    public void createOrder(OrderDomain orderDomain) {
        Order order = toOrder(orderDomain);
        orderMapper.insert(order);
    }

    @Override
    @Transactional
    public OrderDomain getOrderById(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (Objects.isNull(order)) {
            return null;
        }
        return toDomain(orderMapper.selectById(orderId));
    }

    @Override
    @Transactional
    public void updateOrder(OrderDomain orderDomain) {
        Order order = toOrder(orderDomain);
        orderMapper.updateById(order);
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
