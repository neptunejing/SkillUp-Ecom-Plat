package com.skillup.application.order;

import com.alibaba.fastjson.JSON;
import com.skillup.application.promotion.PromotionStockLogServiceApi;
import com.skillup.domain.order.OrderDomain;
import com.skillup.domain.order.OrderService;
import com.skillup.domain.order.util.OrderStatus;
import com.skillup.domain.promotionStockLog.PromotionStockLogDomain;
import com.skillup.domain.promotionStockLog.util.OperationName;
import com.skillup.domain.promotionStockLog.util.OperationStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Slf4j
public class OrderApplication {
    @Autowired
    OrderService orderService;

    @Autowired
    PromotionStockLogServiceApi promotionStockLogServiceApi;

    @Autowired
    MQSendRepo mqSendRepo;

    @Value("${order.topic.create-order}")
    String createOrderTopic;

    @Value("${order.topic.pay-order}")
    String payOrderTopic;

    @Transactional
    public OrderDomain createBuyNowOrder(OrderDomain orderDomain) {
        log.info("OrderApp2.1: create new order [id = {}]", orderDomain.getOrderNumber());
        // 新建流水记录: LOCK_STOCK
        promotionStockLogServiceApi.createPromotionStockLog(toPromotionStockLogDomain(orderDomain, OperationName.LOCK_STOCK));
        // send a message to MQ
        mqSendRepo.sendTxnMsg(createOrderTopic, JSON.toJSONString(orderDomain));
        return orderDomain;
    }

    @Transactional
    public OrderDomain payBuyNowOrder(Long orderNumber, Integer existStatus, Integer expectStatus) {
        OrderDomain orderDomain = orderService.getOrderById(orderNumber);
        if (Objects.isNull(orderDomain)) return null;

        // verify the request
        if (!existStatus.equals(OrderStatus.CREATED.code) || !expectStatus.equals(OrderStatus.PAID.code)) {
            return orderDomain;
        }

        // 新建流水记录: DEDUCT_STOCK
        promotionStockLogServiceApi.createPromotionStockLog(toPromotionStockLogDomain(orderDomain, OperationName.DEDUCT_STOCK));
        if (existStatus.equals(orderDomain.getOrderStatus().code)) {
            mqSendRepo.sendTxnMsg(payOrderTopic, JSON.toJSONString(orderDomain));
        }
        return orderDomain;
    }

    private PromotionStockLogDomain toPromotionStockLogDomain(OrderDomain orderDomain, OperationName operationName) {
        return PromotionStockLogDomain.builder()
                .promotionId(orderDomain.getPromotionId())
                .orderNumber(orderDomain.getOrderNumber())
                .userId(orderDomain.getUserId())
                .operationName(operationName)
                .createTime(LocalDateTime.now())
                .status(OperationStatus.INIT)
                .build();
    }
}