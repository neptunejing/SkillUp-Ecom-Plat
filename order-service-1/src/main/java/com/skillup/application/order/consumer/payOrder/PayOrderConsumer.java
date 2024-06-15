package com.skillup.application.order.consumer.payOrder;

import com.alibaba.fastjson.JSON;
import com.skillup.application.promotion.PromotionServiceApi;
import com.skillup.application.promotion.PromotionStockLogServiceApi;
import com.skillup.domain.order.OrderDomain;
import com.skillup.domain.order.OrderService;
import com.skillup.domain.promotionStockLog.PromotionStockLogDomain;
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
@RocketMQMessageListener(topic = "${order.topic.pay-order}", consumerGroup = "${order.topic.pay-order-group}")
public class PayOrderConsumer implements RocketMQListener<MessageExt> {
    @Autowired
    PromotionStockLogServiceApi promotionStockLogServiceApi;

    @Autowired
    PromotionServiceApi promotionServiceApi;

    @Autowired
    OrderService orderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onMessage(MessageExt messageExt) {
        String messageBody = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        OrderDomain orderDomain = JSON.parseObject(messageBody, OrderDomain.class);
        PromotionStockLogDomain promotionStockLogDomain = promotionStockLogServiceApi.getLogByOrderIdAndOperation(orderDomain.getOrderNumber(), OperationName.DEDUCT_STOCK.toString());
        if (promotionStockLogDomain.getStatus() != OperationStatus.INIT) {
            return;
        }
        try {
            promotionServiceApi.deductPromotionStock(orderDomain.getPromotionId());
            promotionStockLogDomain.setStatus(OperationStatus.CONSUMED);
        } catch (Exception e) {
            promotionStockLogDomain.setStatus(OperationStatus.ROLLBACK);
            log.error("PayOrderConsumer: deduct promotion stock error");
            throw e;
        } finally {
            promotionStockLogServiceApi.updatePromotionStockLog(promotionStockLogDomain);
        }
    }
}
