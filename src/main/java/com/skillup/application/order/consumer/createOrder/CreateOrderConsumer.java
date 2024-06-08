package com.skillup.application.order.consumer.createOrder;

import com.alibaba.fastjson.JSON;
import com.skillup.application.order.MQSendRepo;
import com.skillup.domain.order.OrderDomain;
import com.skillup.domain.promotion.PromotionService;
import com.skillup.domain.promotionStockLog.PromotionStockLogDomain;
import com.skillup.domain.promotionStockLog.PromotionStockLogService;
import com.skillup.domain.promotionStockLog.util.OperationName;
import com.skillup.domain.promotionStockLog.util.OperationStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@RocketMQMessageListener(topic = "${order.topic.create-order}", consumerGroup = "${order.topic.create-order-group}")
public class CreateOrderConsumer implements RocketMQListener<MessageExt> {
    @Autowired
    MQSendRepo mqSendRepo;

    @Autowired
    PromotionService promotionService;

    @Autowired
    PromotionStockLogService promotionStockLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onMessage(MessageExt messageExt) {
        String messageBody = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        OrderDomain orderDomain = JSON.parseObject(messageBody, OrderDomain.class);
        PromotionStockLogDomain promotionStockLogDomain = promotionStockLogService.getLogByOrderIdAndOperation(orderDomain.getOrderNumber(), OperationName.LOCK_STOCK.toString());
        if (promotionStockLogDomain.getStatus() != OperationStatus.INIT) {
            // CONSUMED 说明是重复消费
            return;
        }
        try {
            // write stock info back to DB
            promotionService.lockPromotionStock(orderDomain.getPromotionId());
            // 更新流水
            promotionStockLogDomain.setStatus(OperationStatus.CONSUMED);
        } catch (Exception e) {
            promotionStockLogDomain.setStatus(OperationStatus.ROLLBACK);
            log.error("CreateOrderConsumer: lock promotion stock error");
            throw e;
        } finally {
            promotionStockLogService.updatePromotionStockLog(promotionStockLogDomain);
        }
    }
}
