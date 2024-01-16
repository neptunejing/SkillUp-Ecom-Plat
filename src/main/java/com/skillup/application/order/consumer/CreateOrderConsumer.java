package com.skillup.application.order.consumer;

import com.alibaba.fastjson.JSON;
import com.skillup.application.order.MQSendRepo;
import com.skillup.domain.order.OrderDomain;
import com.skillup.domain.order.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@RocketMQMessageListener(topic = "${order.topic.create-order}", consumerGroup = "${order.topic.create-order-group}")
public class CreateOrderConsumer implements RocketMQListener<MessageExt> {
    @Autowired
    OrderService orderService;

    @Autowired
    MQSendRepo mqSendRepo;

    @Value("${promotion.topic.lock-stock}")
    String lockStockTopic;

    @Override
    public void onMessage(MessageExt messageExt) {
        String messageBody = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        OrderDomain orderDomain = JSON.parseObject(messageBody, OrderDomain.class);
        // 1. save an order to DB
        orderService.createOrder(orderDomain);
        // write stock info back to DB: step 2 and 3 send single messages to two 2 separate listeners
        // 2. send a 'lock-stock' message
        mqSendRepo.sendMsgToTopic(lockStockTopic, JSON.toJSONString(orderDomain));
        log.info("OrderApp: sent lock-stock message. OrderId: " + orderDomain.getOrderNumber());

        // TODO: 3. send a 'pay-check' message
    }
}
