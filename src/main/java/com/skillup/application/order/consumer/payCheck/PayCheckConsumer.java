package com.skillup.application.order.consumer.payCheck;

import com.alibaba.fastjson.JSON;
import com.skillup.application.order.MQSendRepo;
import com.skillup.domain.order.OrderDomain;
import com.skillup.domain.order.OrderService;
import com.skillup.domain.order.util.OrderStatus;
import com.skillup.domain.promotion.PromotionService;
import com.skillup.domain.stock.StockDomain;
import com.skillup.domain.stock.StockService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
@Slf4j
@RocketMQMessageListener(topic = "${order.topic.pay-check}", consumerGroup = "${order.topic.pay-check-group}")
public class PayCheckConsumer implements RocketMQListener<MessageExt> {
    @Autowired
    OrderService orderService;

    @Autowired
    MQSendRepo mqSendRepo;

    @Autowired
    StockService stockService;

    @Autowired
    PromotionService promotionService;

    @Override
    @Transactional
    public void onMessage(MessageExt messageExt) {
        String messageBody = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        OrderDomain orderDomain = JSON.parseObject(messageBody, OrderDomain.class);
        log.info("OrderApp: received pay-check message. OrderId: " + orderDomain.getOrderNumber());
        // 1. retrieve the newest order from DB
        OrderDomain currOrder = orderService.getOrderById(orderDomain.getOrderNumber());
        if (Objects.isNull(currOrder)) {
            throw new RuntimeException("Order doesn't exist.");
        }
        OrderStatus currOrderStatus = currOrder.getOrderStatus();
        // 2. didn't pay within the delay time
        if (currOrderStatus.equals(OrderStatus.CREATED)) {
            // 2.1 update order status
            currOrder.setOrderStatus(OrderStatus.OVERTIME);
            orderService.updateOrder(currOrder);

            // 2.2 revert cached stock
            StockDomain stockDomain = StockDomain.builder().promotionId(orderDomain.getPromotionId()).build();
            stockService.revertAvailableStock(stockDomain);

            // 2.3 revert DB stock by MQ
            promotionService.revertPromotionStock(orderDomain.getPromotionId());
        } else if (currOrderStatus.equals(OrderStatus.PAID)) {
            // 3. paid successfully: stock should be deducted once the payment was done
            log.info("Order (Id: " + orderDomain.getOrderNumber() + ") has been paid successfully");
        } else if (currOrderStatus.equals(OrderStatus.OVERTIME)) {
            // 4. the order is already overtime or cancelled
            log.info("Order (Id: " + orderDomain.getOrderNumber() + ") is already overtime/cancelled");
        }
    }
}
