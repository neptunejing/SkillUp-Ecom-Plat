package com.skillup.infrastructure.rocketMQ.consumer;

import com.alibaba.fastjson.JSON;
import com.skillup.application.order.consumer.CreateOrderEvent;
import com.skillup.domain.order.OrderDomain;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RocketMQMessageListener(topic = "${order.topic.create-order}", consumerGroup = "${order.topic.create-order-group}")
public class CreateOrderConsumer implements RocketMQListener<MessageExt> {
    @Autowired
    ApplicationContext applicationContext;

    @Value("${promotion.topic.lock-stock}")
    String lockStockTopic;

    @Value("${order.topic.pay-check}")
    String payCheckTopic;

    @Override
    public void onMessage(MessageExt messageExt) {
        String messageBody = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        OrderDomain orderDomain = JSON.parseObject(messageBody, OrderDomain.class);
        // create event
        CreateOrderEvent createOrderEvent = new CreateOrderEvent(this, orderDomain);
        // publish event
        applicationContext.publishEvent(createOrderEvent);
    }
}
