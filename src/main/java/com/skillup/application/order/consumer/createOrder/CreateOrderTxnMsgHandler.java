package com.skillup.application.order.consumer.createOrder;

import com.alibaba.fastjson.JSON;
import com.skillup.application.order.MQSendRepo;
import com.skillup.application.order.consumer.TransactionMessageHandler;
import com.skillup.domain.order.OrderDomain;
import com.skillup.domain.order.OrderService;
import com.skillup.domain.order.util.OrderStatus;
import com.skillup.domain.promotionStockLog.PromotionStockLogDomain;
import com.skillup.domain.promotionStockLog.PromotionStockLogService;
import com.skillup.domain.promotionStockLog.util.OperationName;
import com.skillup.domain.promotionStockLog.util.OperationStatus;
import com.skillup.domain.stock.StockDomain;
import com.skillup.domain.stock.StockService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;

@Component("CreateOrderTxnMsgHandler")
@Slf4j
public class CreateOrderTxnMsgHandler implements TransactionMessageHandler {
    @Autowired
    MQSendRepo mqSendRepo;

    @Autowired
    StockService stockService;

    @Autowired
    PromotionStockLogService promotionStockLogService;

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Object payload, Object arg) {
        String messageBody = new String((byte[]) payload, StandardCharsets.UTF_8);
        OrderDomain orderDomain = JSON.parseObject(messageBody, OrderDomain.class);
        // lock cached promotion stock
        StockDomain stockDomain = StockDomain.builder().promotionId(orderDomain.getPromotionId()).build();
        try {
            boolean isLocked = stockService.lockAvailableStock(stockDomain);
            if (!isLocked) {
                log.info("CreateOrderTxnMsg Rollback");
                throw new RuntimeException();
            }
        } catch (Exception e) {
            stockService.revertAvailableStock(stockDomain);
            log.info("CreateOrderTxnMsg Rollback");
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        return RocketMQLocalTransactionState.COMMIT;
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Object payload) {
        String messageBody = new String((byte[]) payload, StandardCharsets.UTF_8);
        OrderDomain orderDomain = JSON.parseObject(messageBody, OrderDomain.class);
        // 检查流水表
        PromotionStockLogDomain promotionStockLogDomain = promotionStockLogService.getLogByOrderIdAndOperation(orderDomain.getOrderNumber(), OperationName.LOCK_STOCK.toString());
        if (Objects.isNull(promotionStockLogDomain)) {
            return RocketMQLocalTransactionState.UNKNOWN;
        }
        if (promotionStockLogDomain.getStatus() == OperationStatus.CONSUMED) {
            return RocketMQLocalTransactionState.COMMIT;
        }
        if (promotionStockLogDomain.getStatus() == OperationStatus.INIT) {
            return RocketMQLocalTransactionState.UNKNOWN;
        }
        return RocketMQLocalTransactionState.ROLLBACK;
    }
}
