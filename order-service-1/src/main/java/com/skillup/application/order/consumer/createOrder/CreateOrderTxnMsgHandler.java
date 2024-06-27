package com.skillup.application.order.consumer.createOrder;

import com.alibaba.fastjson.JSON;
import com.skillup.application.order.MQSendRepo;
import com.skillup.application.order.consumer.TransactionMessageHandler;
import com.skillup.application.order.consumer.createOrder.util.OrderErrorLogHandler;
import com.skillup.application.promotion.StockServiceApi;
import com.skillup.domain.order.OrderDomain;
import com.skillup.domain.order.OrderService;
import com.skillup.domain.order.util.OrderStatus;
import com.skillup.domain.promotionStockLog.util.OperationName;
import com.skillup.domain.stock.StockDomain;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    StockServiceApi stockServiceApi;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderErrorLogHandler orderErrorLogHandler;

    @Value("${order.topic.pay-check}")
    String payCheckTopic;

    @Value("${order.delay-time}")
    int delaySeconds;

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Object payload, Object arg) {
        String messageBody = new String((byte[]) payload, StandardCharsets.UTF_8);
        OrderDomain orderDomain = JSON.parseObject(messageBody, OrderDomain.class);
        // lock cached promotion stock
        StockDomain stockDomain = StockDomain.builder()
                .promotionId(orderDomain.getPromotionId())
                .orderId(orderDomain.getOrderNumber())
                .operationName(OperationName.LOCK_STOCK)
                .build();
        boolean isLocked = stockServiceApi.lockAvailableStock(stockDomain);
        if (!isLocked) {
            log.warn("[OrderApp1.1] out of stock! CreateOrderTxnMsg Rollback");
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        try {
            orderDomain.setCreateTime(LocalDateTime.now());
            orderDomain.setOrderStatus(OrderStatus.CREATED);
            orderService.createOrder(orderDomain);
            // send a 'pay-check' message
            mqSendRepo.sendDelayMsgToTopic(payCheckTopic, JSON.toJSONString(orderDomain), delaySeconds);
            log.info("[OrderApp1.1] sent pay-check message. OrderId: {}", orderDomain.getOrderNumber());
        } catch (Exception e) {
            stockDomain.setOperationName(OperationName.REVERT_STOCK);
            stockServiceApi.revertAvailableStock(stockDomain);
            orderErrorLogHandler.log(orderDomain.getOrderNumber());
            log.error("[OrderApp1.1] create order error. OrderId: {}", orderDomain.getOrderNumber());
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        return RocketMQLocalTransactionState.COMMIT;
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Object payload) {
        String messageBody = new String((byte[]) payload, StandardCharsets.UTF_8);
        OrderDomain orderDomain = JSON.parseObject(messageBody, OrderDomain.class);
        // redis 存在订单创建失败记录，告知 broker 回滚
        if (orderErrorLogHandler.ifErrorLogExists(orderDomain.getOrderNumber())) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        OrderDomain existedOrderDomain = orderService.getOrderById(orderDomain.getOrderNumber());
        if (!Objects.isNull(existedOrderDomain)) {
            // DB 存在订单，告知 broker 提交
            return RocketMQLocalTransactionState.COMMIT;
        }
        // 否则继续回查
        return RocketMQLocalTransactionState.UNKNOWN;
    }
}
