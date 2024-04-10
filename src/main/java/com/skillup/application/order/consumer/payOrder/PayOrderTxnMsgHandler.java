package com.skillup.application.order.consumer.payOrder;

import com.alibaba.fastjson.JSON;
import com.skillup.application.order.consumer.TransactionMessageHandler;
import com.skillup.domain.order.OrderDomain;
import com.skillup.domain.order.OrderService;
import com.skillup.domain.order.util.OrderStatus;
import com.skillup.domain.promotionStockLog.PromotionStockLogDomain;
import com.skillup.domain.promotionStockLog.PromotionStockLogService;
import com.skillup.domain.promotionStockLog.util.OperationName;
import com.skillup.domain.promotionStockLog.util.OperationStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;

@Component("PayOrderTxnMsgHandler")
@Slf4j
public class PayOrderTxnMsgHandler implements TransactionMessageHandler {
    @Autowired
    OrderService orderService;

    @Autowired
    PromotionStockLogService promotionStockLogService;

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Object payload, Object arg) {
        String messageBody = new String((byte[]) payload, StandardCharsets.UTF_8);
        OrderDomain orderDomain = JSON.parseObject(messageBody, OrderDomain.class);
        try {
            boolean isPaid = thirdPartyPayment();
            if (!isPaid) {
                throw new RuntimeException();
            }
            orderDomain.setOrderStatus(OrderStatus.PAID);
            orderDomain.setPayTime(LocalDateTime.now());
            orderService.updateOrder(orderDomain);
        } catch (Exception e) {
            log.info("PayOrderTxnMsg Rollback");
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        return RocketMQLocalTransactionState.COMMIT;
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Object payload) {
        String messageBody = new String((byte[]) payload, StandardCharsets.UTF_8);
        OrderDomain orderDomain = JSON.parseObject(messageBody, OrderDomain.class);
        // 检查流水表
        PromotionStockLogDomain promotionStockLogDomain = promotionStockLogService.getLogByOrderIdAndOperation(orderDomain.getOrderNumber(), OperationName.DEDUCT_STOCK.toString());
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

    private boolean thirdPartyPayment() {
        return true;
    }

    private PromotionStockLogDomain toPromotionStockLogDomain(OrderDomain orderDomain) {
        return PromotionStockLogDomain.builder()
                .promotionId(orderDomain.getPromotionId())
                .orderNumber(orderDomain.getOrderNumber())
                .userId(orderDomain.getUserId())
                .operationName(OperationName.DEDUCT_STOCK)
                .createTime(LocalDateTime.now())
                .status(OperationStatus.INIT)
                .build();
    }
}
