package com.skillup.application.promotion.consumer;

import com.alibaba.fastjson.JSON;
import com.skillup.domain.order.OrderDomain;
import com.skillup.domain.promotion.PromotionService;
import com.skillup.domain.promotionStockLog.PromotionStockLogDomain;
import com.skillup.domain.promotionStockLog.PromotionStockLogService;
import com.skillup.domain.promotionStockLog.util.OperationName;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;

@Component
@Slf4j
@RocketMQMessageListener(topic = "${promotion.topic.lock-stock}", consumerGroup = "${promotion.topic.lock-stock-group}")
public class LockStockConsumer implements RocketMQListener<MessageExt> {
    @Autowired
    PromotionService promotionService;

    @Autowired
    PromotionStockLogService promotionStockLogService;

    @Override
    @Transactional
    public void onMessage(MessageExt messageExt) {
        String messageBody = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        OrderDomain orderDomain = JSON.parseObject(messageBody, OrderDomain.class);
        PromotionStockLogDomain promotionStockLogDomain = promotionStockLogService.getLogByOrderIdAndOperation(orderDomain.getOrderNumber(), OperationName.LOCK_STOCK.toString());
        // keep idempotent
        if (!Objects.isNull(promotionStockLogDomain)) {
            log.info("PromotionApp: received duplicate lock-stock message. OrderId: " + orderDomain.getOrderNumber());
            return;
        }
        promotionService.lockPromotionStock(orderDomain.getPromotionId());
        promotionStockLogService.createPromotionStockLog(toPromotionStockLogDomain(orderDomain));
        log.info("PromotionApp: received lock-stock message. OrderId: " + orderDomain.getOrderNumber());
    }

    private PromotionStockLogDomain toPromotionStockLogDomain(OrderDomain orderDomain) {
        return PromotionStockLogDomain.builder()
                .promotionId(orderDomain.getPromotionId())
                .orderNumber(orderDomain.getOrderNumber())
                .userId(orderDomain.getUserId())
                .operationName(OperationName.LOCK_STOCK)
                .createTime(LocalDateTime.now())
                .build();
    }
}
