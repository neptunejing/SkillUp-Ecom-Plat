package com.skillup.application.promotion.consumer;


import com.alibaba.fastjson.JSON;
import com.skillup.application.promotion.PromotionServiceApi;
import com.skillup.application.promotion.PromotionStockLogServiceApi;
import com.skillup.domain.order.OrderDomain;
import com.skillup.domain.promotionStockLog.PromotionStockLogDomain;
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
@RocketMQMessageListener(topic = "${promotion.topic.deduct-stock}", consumerGroup = "${promotion.topic.deduct-stock-group}")
public class DeductStockConsumer implements RocketMQListener<MessageExt> {
    @Autowired
    PromotionServiceApi promotionServiceApi;

    @Autowired
    PromotionStockLogServiceApi promotionStockLogServiceApi;

    @Override
    @Transactional
    public void onMessage(MessageExt messageExt) {
        String messageBody = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        OrderDomain orderDomain = JSON.parseObject(messageBody, OrderDomain.class);
        PromotionStockLogDomain promotionStockLogDomain = promotionStockLogServiceApi.getLogByOrderIdAndOperation(orderDomain.getOrderNumber(), OperationName.DEDUCT_STOCK.toString());
        // 幂等性检查：流水记录非 INIT 就直接返回
        if (!Objects.isNull(promotionStockLogDomain)) {
            return;
        }
        log.info("PromotionApp: received deduct-stock message. OrderId: " + orderDomain.getOrderNumber());
        promotionServiceApi.deductPromotionStock(orderDomain.getPromotionId());
        promotionStockLogServiceApi.createPromotionStockLog(toPromotionStockLogDomain(orderDomain));
    }

    private PromotionStockLogDomain toPromotionStockLogDomain(OrderDomain orderDomain) {
        return PromotionStockLogDomain.builder()
                .promotionId(orderDomain.getPromotionId())
                .orderNumber(orderDomain.getOrderNumber())
                .userId(orderDomain.getUserId())
                .operationName(OperationName.DEDUCT_STOCK)
                .createTime(LocalDateTime.now())
                .build();
    }
}