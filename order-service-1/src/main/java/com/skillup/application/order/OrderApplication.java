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
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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

    @Value("${promotion.topic.deduct-stock}")
    String deductStockTopic;

    @Transactional
    public OrderDomain createBuyNowOrder(OrderDomain orderDomain) {
        log.info("OrderApp1.1: create new order [id = {}]", orderDomain.getOrderNumber());
        // 新建流水记录: LOCK_STOCK
        promotionStockLogServiceApi.createPromotionStockLog(toPromotionStockLogDomain(orderDomain, OperationName.LOCK_STOCK));
        mqSendRepo.sendMsgToTopic(createOrderTopic, JSON.toJSONString(orderDomain));
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

        // 事务内 SELECT FOR UPDATE 保证「查 + 改」原子性
        OrderDomain existedOrderDomain = orderService.getOrderById(orderDomain.getOrderNumber());
        if (!existedOrderDomain.getOrderStatus().equals(OrderStatus.CREATED)) {
            // 订单处于 PAYING/PAID/OVERTIME，直接返回，避免重复支付
            return orderDomain;
        }

        // 新建流水记录: DEDUCT_STOCK
        promotionStockLogServiceApi.createPromotionStockLog(toPromotionStockLogDomain(orderDomain, OperationName.DEDUCT_STOCK));
        if (existStatus.equals(orderDomain.getOrderStatus().code)) {
            mqSendRepo.sendMsgToTopic(payOrderTopic, JSON.toJSONString(orderDomain));
        }

        // 模拟支付过程
        orderDomain.setOrderStatus(OrderStatus.PAYING);
        orderService.updateOrder(orderDomain);
        try {
            CompletableFuture<Boolean> isPaid = thirdPartyPayment();
            // 令支付最多等待 3s，否则抛出 TimeoutException
            boolean paymentSuccess = isPaid.get(3, TimeUnit.SECONDS);
            if (paymentSuccess) {
                orderDomain.setOrderStatus(OrderStatus.PAID);
                orderDomain.setPayTime(LocalDateTime.now());
                orderService.updateOrder(orderDomain);
            } else {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            log.error("[Payment Error]" + e.getMessage());
            orderDomain.setOrderStatus(OrderStatus.ITEM_ERROR);
            orderService.updateOrder(orderDomain);
        }

        mqSendRepo.sendMsgToTopic(deductStockTopic, JSON.toJSONString(orderDomain));
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

    // 模拟异步支付请求
    private CompletableFuture<Boolean> thirdPartyPayment() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 模拟支付处理时间
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            // 模拟支付成功
            return true;
        });
    }
}