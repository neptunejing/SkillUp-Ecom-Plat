package com.skillup.application.order;

import com.alibaba.fastjson.JSON;
import com.skillup.application.promotion.PromotionApplication;
import com.skillup.domain.order.OrderDomain;
import com.skillup.domain.order.OrderService;
import com.skillup.domain.order.util.OrderStatus;
import com.skillup.domain.promotion.PromotionDomain;
import com.skillup.domain.stock.StockDomain;
import com.skillup.domain.stock.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class OrderApplication {
    @Autowired
    OrderService orderService;

    @Autowired
    PromotionApplication promotionApplication;

    @Autowired
    StockService stockService;

    @Autowired
    MQSendRepo mqSendRepo;

    @Value("${order.topic.create-order}")
    String createOrderTopic;

    @Value("${promotion.topic.deduct-stock}")
    String deductStockTopic;

    @Transactional
    public OrderDomain createBuyNowOrder(OrderDomain orderDomain) {
        // 1. check if a promotion id exists in promotion cache
        PromotionDomain promotionDomain = promotionApplication.getPromotionById(orderDomain.getPromotionId());
        if (Objects.isNull(promotionDomain)) {
            orderDomain.setOrderStatus(OrderStatus.ITEM_ERROR);
            return orderDomain;
        }
        // 2. lock cached promotion stock
        StockDomain stockDomain = StockDomain.builder().promotionId(orderDomain.getPromotionId()).build();
        boolean isLocked = stockService.lockAvailableStock(stockDomain);
        if (!isLocked) {
            orderDomain.setOrderStatus(OrderStatus.OUT_OF_STOCK);
            return orderDomain;
        }
        // 3. create an order
        orderDomain.setCreateTime(LocalDateTime.now());
        orderDomain.setOrderStatus(OrderStatus.CREATED);
        // 4. send a message to MQ
        mqSendRepo.sendMsgToTopic(createOrderTopic, JSON.toJSONString(orderDomain));
        return orderDomain;
    }

    public OrderDomain payBuyNowOrder(Long orderNumber, Integer existStatus, Integer expectStatus) {
        OrderDomain orderDomain = orderService.getOrderById(orderNumber);
        if (Objects.isNull(orderDomain)) return null;

        // verify the request
        if (!existStatus.equals(OrderStatus.CREATED.code) || !expectStatus.equals(OrderStatus.PAID.code)) {
            return orderDomain;
        }

        if (existStatus.equals(orderDomain.getOrderStatus().code)) {
            boolean isPaid = thirdPartyPayment();
            if (!isPaid) return orderDomain;

            orderDomain.setOrderStatus(OrderStatus.PAID);
            orderDomain.setPayTime(LocalDateTime.now());
            orderService.updateOrder(orderDomain);

            // deduct DB stock by MQ
            mqSendRepo.sendMsgToTopic(deductStockTopic, JSON.toJSONString(orderDomain));
        }
        return orderDomain;
    }

    private boolean thirdPartyPayment() {
        return true;
    }
}