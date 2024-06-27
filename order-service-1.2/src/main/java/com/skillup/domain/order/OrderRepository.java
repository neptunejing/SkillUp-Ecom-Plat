package com.skillup.domain.order;

public interface OrderRepository {
    void createOrder(OrderDomain orderDomain);

    OrderDomain getOrderById(Long orderId);

    void updateOrder(OrderDomain orderDomain);
}
