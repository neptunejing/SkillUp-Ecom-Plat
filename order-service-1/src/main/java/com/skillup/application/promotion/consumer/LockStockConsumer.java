package com.skillup.application.promotion.consumer;

import com.alibaba.fastjson.JSON;
import com.skillup.application.promotion.PromotionServiceApi;
import com.skillup.application.promotion.PromotionStockLogServiceApi;
import com.skillup.application.promotion.StockServiceApi;
import com.skillup.domain.order.OrderDomain;
import com.skillup.domain.promotionStockLog.PromotionStockLogDomain;
import com.skillup.domain.promotionStockLog.util.OperationName;
import com.skillup.domain.promotionStockLog.util.OperationStatus;
import com.skillup.domain.stock.StockDomain;
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
@RocketMQMessageListener(topic = "${promotion.topic.lock-stock}", consumerGroup = "${promotion.topic.lock-stock-group}")
public class LockStockConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    PromotionServiceApi promotionServiceApi;

    @Autowired
    PromotionStockLogServiceApi promotionStockLogServiceApi;

    @Autowired
    StockServiceApi stockServiceApi;

    @Override
    @Transactional()
    public void onMessage(MessageExt messageExt) {
        String messageBody = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        OrderDomain orderDomain = JSON.parseObject(messageBody, OrderDomain.class);
        PromotionStockLogDomain promotionStockLogDomain = promotionStockLogServiceApi.getLogByOrderIdAndOperation(orderDomain.getOrderNumber(), OperationName.LOCK_STOCK.toString());
        // 幂等性检查：流水记录非 INIT 就直接返回
        if (promotionStockLogDomain.getStatus() != OperationStatus.INIT) {
            return;
        }
        try {
            // 扣减数据库库存
            promotionServiceApi.lockPromotionStock(orderDomain.getPromotionId());
            promotionStockLogDomain.setStatus(OperationStatus.CONSUMED);
        } catch (Exception e) {
            // 回滚预扣库存操作
            promotionStockLogDomain.setStatus(OperationStatus.ROLLBACK);
            revertCachedStock(orderDomain);
            log.error("CreateOrderConsumer: lock promotion stock error");
        } finally {
            promotionStockLogServiceApi.updatePromotionStockLog(promotionStockLogDomain);
        }
    }

    private void revertCachedStock(OrderDomain orderDomain) {
        StockDomain stockDomain = StockDomain.builder()
                .promotionId(orderDomain.getPromotionId())
                .orderId(orderDomain.getOrderNumber())
                .operationName(OperationName.REVERT_STOCK)
                .build();
        stockServiceApi.revertAvailableStock(stockDomain);
    }
}
