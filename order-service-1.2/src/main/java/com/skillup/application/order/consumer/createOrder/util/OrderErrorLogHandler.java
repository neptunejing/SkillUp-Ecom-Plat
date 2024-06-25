package com.skillup.application.order.consumer.createOrder.util;

import com.skillup.infrastructure.redis.OrderRedisRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderErrorLogHandler {
    @Autowired
    OrderRedisRepo orderRedisRepo;

    private final String ERROR_LOG_SET_KEY = "CO_ERROR";

    public void log(Long orderId) {
        orderRedisRepo.addToSet(ERROR_LOG_SET_KEY, String.valueOf(orderId));
    }

    public boolean ifErrorLogExists(Long orderId) {
        return orderRedisRepo.getFromSet(ERROR_LOG_SET_KEY, String.valueOf(orderId));
    }
}
