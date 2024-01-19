package com.skillup.application.promotion.consumer;

import com.alibaba.fastjson.JSON;
import com.skillup.domain.order.OrderDomain;
import com.skillup.domain.promotion.PromotionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@RocketMQMessageListener(topic = "${promotion.topic.revert-stock}", consumerGroup = "${promotion.topic.revert-stock-group}")
public class RevertStockConsumer implements RocketMQListener<MessageExt> {
    @Autowired
    PromotionService promotionService;

    @Override
    public void onMessage(MessageExt messageExt) {
        String messageBody = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        OrderDomain orderDomain = JSON.parseObject(messageBody, OrderDomain.class);
        log.info("PromotionApp: received revert-stock message. OrderId: " + orderDomain.getOrderNumber());
        promotionService.revertPromotionStock(orderDomain.getPromotionId());
    }
}
