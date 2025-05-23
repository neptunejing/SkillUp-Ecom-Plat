package com.skillup.domain.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    @Autowired
    @Qualifier("batisOrderRepo")
    OrderRepository orderRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public OrderDomain createOrder(OrderDomain orderDomain) {
        orderRepository.createOrder(orderDomain);
        return orderDomain;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrderDomain getOrderById(Long orderId) {
        return orderRepository.getOrderById(orderId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrderDomain updateOrder(OrderDomain orderDomain) {
        orderRepository.updateOrder(orderDomain);
        return orderDomain;
    }
}
