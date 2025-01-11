package com.skillup.application.order.consumer;

import com.alibaba.fastjson.JSON;
import com.skillup.application.order.MQSendRepo;
import com.skillup.application.promotion.StockServiceApi;
import com.skillup.domain.order.OrderDomain;
import com.skillup.domain.order.OrderService;
import com.skillup.domain.order.util.OrderStatus;
import com.skillup.domain.promotionStockLog.util.OperationName;
import com.skillup.domain.stock.StockDomain;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;

@Component
@Slf4j
@RocketMQMessageListener(topic = "${order.topic.create-order}", consumerGroup = "${order.topic.create-order-group}")
public class CreateOrderConsumer implements RocketMQListener<MessageExt> {
    @Autowired
    OrderService orderService;

    @Autowired
    StockServiceApi stockServiceApi;

    @Autowired
    MQSendRepo mqSendRepo;

    @Value("${promotion.topic.lock-stock}")
    String lockStockTopic;

    @Value("${order.topic.pay-check}")
    String payCheckTopic;

    @Value("${order.delay-time}")
    int delaySeconds;

    @Override
    public void onMessage(MessageExt messageExt) {
        String messageBody = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        OrderDomain orderDomain = JSON.parseObject(messageBody, OrderDomain.class);

        // 幂等性检查：如果已经相同 orderId 的订单，直接返回
        OrderDomain existedOrderDomain = orderService.getOrderById(orderDomain.getOrderNumber());
        if (!Objects.isNull(existedOrderDomain)) {
            log.warn("Duplicate consumption. Order exists [id = {}].", existedOrderDomain.getOrderNumber());
            return;
        }

        // 预扣减 Redis 库存
        StockDomain stockDomain = StockDomain.builder()
                .promotionId(orderDomain.getPromotionId())
                .orderId(orderDomain.getOrderNumber())
                .operationName(OperationName.LOCK_STOCK)
                .build();
        boolean isLocked = stockServiceApi.lockAvailableStock(stockDomain);
        if (!isLocked) {
            log.warn("Lock cached stock failed.");
            orderDomain.setOrderStatus(OrderStatus.OUT_OF_STOCK);
            return;
        }

        // 创建订单
        try {
            orderDomain.setCreateTime(LocalDateTime.now());
            orderDomain.setOrderStatus(OrderStatus.CREATED);
            orderService.createOrder(orderDomain);
        } catch (Exception e) {
            // 回滚锁定 redis 库存操作
            log.error("Create order failed. {}", e.getMessage());
            stockDomain.setOperationName(OperationName.REVERT_STOCK);
            stockServiceApi.revertAvailableStock(stockDomain);
            return;
        }

        mqSendRepo.sendMsgToTopic(lockStockTopic, JSON.toJSONString(orderDomain));
        mqSendRepo.sendDelayMsgToTopic(payCheckTopic, JSON.toJSONString(orderDomain), delaySeconds);
    }
}