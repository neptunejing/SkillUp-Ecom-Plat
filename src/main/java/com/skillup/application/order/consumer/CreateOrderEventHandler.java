package com.skillup.application.order.consumer;

import com.alibaba.fastjson.JSON;
import com.skillup.application.order.MQSendRepo;
import com.skillup.domain.order.OrderDomain;
import com.skillup.domain.order.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CreateOrderEventHandler implements ApplicationListener<CreateOrderEvent> {
    @Autowired
    OrderService orderService;

    @Autowired
    MQSendRepo mqSendRepo;

    @Value("${promotion.topic.lock-stock}")
    String lockStockTopic;

    @Value("${order.topic.pay-check}")
    String payCheckTopic;

    @Override
    public void onApplicationEvent(CreateOrderEvent event) {
        OrderDomain orderDomain = event.orderDomain;
        // 1. save an order to DB
        orderService.createOrder(orderDomain);
        // write stock info back to DB: step 2 and 3 send single messages to two 2 separate listeners
        // 2. send a 'lock-stock' message
        mqSendRepo.sendMsgToTopic(lockStockTopic, JSON.toJSONString(orderDomain));
        log.info("OrderApp: sent lock-stock message. OrderId: " + orderDomain.getOrderNumber());

        // 3. send a 'pay-check' message
        mqSendRepo.sendDelayMsgToTopic(payCheckTopic, JSON.toJSONString(orderDomain));
        log.info("OrderApp: sent pay-check message. OrderId: " + orderDomain.getOrderNumber());
    }
}
