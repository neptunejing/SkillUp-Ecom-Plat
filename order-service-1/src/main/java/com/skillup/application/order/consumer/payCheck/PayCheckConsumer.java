package com.skillup.application.order.consumer.payCheck;

import com.alibaba.fastjson.JSON;
import com.skillup.application.order.MQSendRepo;
import com.skillup.application.promotion.PromotionServiceApi;
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
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
@Slf4j
@RocketMQMessageListener(topic = "${order.topic.pay-check}", consumerGroup = "${order.topic.pay-check-group}")
public class PayCheckConsumer implements RocketMQListener<MessageExt> {
    @Autowired
    OrderService orderService;

    @Autowired
    MQSendRepo mqSendRepo;

    @Autowired
    StockServiceApi stockServiceApi;

    @Autowired
    PromotionServiceApi promotionServiceApi;

    @Value("${order.topic.pay-check}")
    String payCheckTopic;

    @Value("${order.delay-time}")
    Integer delaySeconds;

    @Override
    @Transactional
    public void onMessage(MessageExt messageExt) {
        String messageBody = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        OrderDomain orderDomain = JSON.parseObject(messageBody, OrderDomain.class);
        log.info("OrderApp: received pay-check message. OrderId: " + orderDomain.getOrderNumber());
        OrderDomain currOrder = orderService.getOrderById(orderDomain.getOrderNumber());
        if (Objects.isNull(currOrder)) {
            throw new RuntimeException("Order doesn't exist.");
        }
        OrderStatus currOrderStatus = currOrder.getOrderStatus();
        // didn't pay within the delay time
        if (currOrderStatus.equals(OrderStatus.CREATED)) {
            // update order status
            currOrder.setOrderStatus(OrderStatus.OVERTIME);
            orderService.updateOrder(currOrder);
            // revert cached stock
            StockDomain stockDomain = StockDomain.builder()
                    .promotionId(orderDomain.getPromotionId())
                    .orderId(orderDomain.getOrderNumber())
                    .operationName(OperationName.REVERT_STOCK)
                    .build();
            stockServiceApi.revertAvailableStock(stockDomain);
            // revert DB stock
            promotionServiceApi.revertPromotionStock(orderDomain.getPromotionId());
        } else if (currOrderStatus.equals(OrderStatus.PAID)) {
            // paid successfully: stock should be deducted once the payment was done
            log.info("Order (Id: " + orderDomain.getOrderNumber() + ") has been paid successfully");
        } else if (currOrderStatus.equals(OrderStatus.PAYING)) {
            // 如果时延检查遇到 paying 状态，以 delaySeconds / 10 为延迟再次发送时延消息
            // 获取异步支付结果的 get() 设置为 3s 后返回并认定失败，delaySeconds / 10 后订单要么支付成功要么失败
            mqSendRepo.sendDelayMsgToTopic(payCheckTopic, JSON.toJSONString(orderDomain), delaySeconds / 10);
        } else {
            // the order is already overtime or cancelled
            log.info("Order (Id: " + orderDomain.getOrderNumber() + ") is already overtime/cancelled");
        }
    }
}
