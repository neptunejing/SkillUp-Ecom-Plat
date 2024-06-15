package com.skillup.application.order.consumer.payOrder;

import com.alibaba.fastjson.JSON;
import com.skillup.application.order.consumer.TransactionMessageHandler;
import com.skillup.domain.order.OrderDomain;
import com.skillup.domain.order.OrderService;
import com.skillup.domain.order.util.OrderStatus;
import com.skillup.domain.promotionStockLog.PromotionStockLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component("PayOrderTxnMsgHandler")
@Slf4j
public class PayOrderTxnMsgHandler implements TransactionMessageHandler {
    @Autowired
    PromotionStockLogService promotionStockLogService;

    @Autowired
    OrderService orderService;

    @Override
    @Transactional
    public RocketMQLocalTransactionState executeLocalTransaction(Object payload, Object arg) {
        String messageBody = new String((byte[]) payload, StandardCharsets.UTF_8);
        OrderDomain orderDomain = JSON.parseObject(messageBody, OrderDomain.class);
        // 事务内 SELECT FOR UPDATE 保证「查 + 改」原子性
        OrderDomain existedOrderDomain = orderService.getOrderById(orderDomain.getOrderNumber());
        if (!existedOrderDomain.getOrderStatus().equals(OrderStatus.CREATED)) {
            // 订单处于 PAYING/PAID/OVERTIME，直接返回，避免重复支付
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        orderDomain.setOrderStatus(OrderStatus.PAYING);
        orderService.updateOrder(orderDomain);
        try {
            CompletableFuture<Boolean> isPaid = thirdPartyPayment();
            // 令支付最多等待 3s，否则抛出 TimeoutException
            boolean paymentSuccess = isPaid.get(3, TimeUnit.SECONDS);
            if (paymentSuccess) {
                orderDomain.setOrderStatus(OrderStatus.PAID);
                orderDomain.setPayTime(LocalDateTime.now());
                orderService.updateOrder(orderDomain);
                return RocketMQLocalTransactionState.COMMIT;
            } else {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            log.error("[Payment Error]" + e.getMessage());
            orderDomain.setOrderStatus(OrderStatus.ITEM_ERROR);
            orderService.updateOrder(orderDomain);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Object payload) {
        String messageBody = new String((byte[]) payload, StandardCharsets.UTF_8);
        OrderDomain orderDomain = JSON.parseObject(messageBody, OrderDomain.class);
        OrderDomain existedOrderDomain = orderService.getOrderById(orderDomain.getOrderNumber());
        OrderStatus currStatus = existedOrderDomain.getOrderStatus();
        if (currStatus.equals(OrderStatus.PAID)) {
            return RocketMQLocalTransactionState.COMMIT;
        }
        if (currStatus.equals(OrderStatus.PAYING)) {
            return RocketMQLocalTransactionState.UNKNOWN;
        }
        // OrderStatus.OVERTIME || OrderStatus.ITEM_ERROR
        return RocketMQLocalTransactionState.ROLLBACK;
    }

    // 模拟异步支付请求
    public CompletableFuture<Boolean> thirdPartyPayment() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 模拟支付处理时间
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            // 模拟支付成功
            return true;
        });
    }
}
