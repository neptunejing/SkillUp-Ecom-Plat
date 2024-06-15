package com.skillup.application.order.consumer.createOrder;

import com.alibaba.fastjson.JSON;
import com.skillup.application.order.MQSendRepo;
import com.skillup.application.order.consumer.TransactionMessageHandler;
import com.skillup.application.promotion.StockServiceApi;
import com.skillup.domain.order.OrderDomain;
import com.skillup.domain.order.OrderService;
import com.skillup.domain.order.util.OrderStatus;
import com.skillup.domain.stock.StockDomain;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;

@Component("CreateOrderTxnMsgHandler")
@Slf4j
public class CreateOrderTxnMsgHandler implements TransactionMessageHandler {
    @Autowired
    MQSendRepo mqSendRepo;

    @Autowired
    StockServiceApi stockServiceApi;

    @Autowired
    OrderService orderService;

    @Value("${order.topic.pay-check}")
    String payCheckTopic;

    @Value("${order.delay-time}")
    int delaySeconds;

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Object payload, Object arg) {
        String messageBody = new String((byte[]) payload, StandardCharsets.UTF_8);
        OrderDomain orderDomain = JSON.parseObject(messageBody, OrderDomain.class);
        // lock cached promotion stock
        StockDomain stockDomain = StockDomain.builder().promotionId(orderDomain.getPromotionId()).build();
        boolean isLocked = stockServiceApi.lockAvailableStock(stockDomain);
        if (!isLocked) {
            log.info("[Out of Stock] CreateOrderTxnMsg Rollback");
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        try {
            orderDomain.setCreateTime(LocalDateTime.now());
            orderDomain.setOrderStatus(OrderStatus.CREATED);
            orderService.createOrder(orderDomain);
            // send a 'pay-check' message
            mqSendRepo.sendDelayMsgToTopic(payCheckTopic, JSON.toJSONString(orderDomain), delaySeconds);
            log.info("OrderApp: sent pay-check message. OrderId: " + orderDomain.getOrderNumber());
        } catch (Exception e) {
            stockServiceApi.revertAvailableStock(stockDomain);
            log.info("[Create Order Error] CreateOrderTxnMsg Rollback");
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        return RocketMQLocalTransactionState.COMMIT;
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Object payload) {
        String messageBody = new String((byte[]) payload, StandardCharsets.UTF_8);
        OrderDomain orderDomain = JSON.parseObject(messageBody, OrderDomain.class);
        OrderDomain existedOrderDomain = orderService.getOrderById(orderDomain.getOrderNumber());
        if (!Objects.isNull(existedOrderDomain)) {
            // 订单存在，直接 COMMIT
            return RocketMQLocalTransactionState.COMMIT;
        }
        // 否则 ROLLBACK
        return RocketMQLocalTransactionState.ROLLBACK;
    }
}
