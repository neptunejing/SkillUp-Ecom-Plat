package com.skillup.api;

import com.skillup.api.dto.in.OrderInDto;
import com.skillup.api.dto.in.OrderStatusInDto;
import com.skillup.api.dto.out.OrderOutDto;
import com.skillup.api.util.SkillUpCommon;
import com.skillup.api.util.Snowflake;
import com.skillup.application.order.OrderApplication;
import com.skillup.domain.order.OrderDomain;
import com.skillup.domain.order.OrderService;
import com.skillup.domain.order.util.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/order")
public class OrderController {
    @Autowired
    Snowflake snowflake;

    @Autowired
    OrderApplication orderApplication;

    @Autowired
    OrderService orderService;


    // BuyNowOrder 即直接下单，不经过放入 cart 的步骤
    @PostMapping()
    public ResponseEntity<OrderOutDto> createBuyNowOrder(@RequestBody OrderInDto orderInDto) {
        if (!orderApplication.ifPromotionMightContain(orderInDto.getPromotionId())) {
            // 缓存中不存在该秒杀商品 id
            log.info("-----[Promotion BloomFilter]: PromotionId doesn't exist -----");
            return ResponseEntity.status(SkillUpCommon.BAD_REQUEST).body(null);
        }
        OrderDomain orderDomain = orderApplication.createBuyNowOrder(toDomain(orderInDto));
        return ResponseEntity.status(SkillUpCommon.SUCCESS).body(toOutDto(orderDomain));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<OrderOutDto> getOrderById(@PathVariable("id") Long orderId) {
        OrderDomain orderDomain = orderService.getOrderById(orderId);
        if (Objects.isNull(orderDomain)) {
            return ResponseEntity.status(SkillUpCommon.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(SkillUpCommon.SUCCESS).body(toOutDto(orderDomain));
    }

    @PatchMapping("/pay")
    public ResponseEntity<OrderOutDto> payBuyNowOrder(@RequestBody OrderStatusInDto orderStatusInDto) throws ExecutionException, InterruptedException {
        OrderDomain orderDomain = orderApplication.payBuyNowOrder(orderStatusInDto.getOrderNumber(), orderStatusInDto.getExistStatus(), orderStatusInDto.getExpectStatus());
        if (Objects.isNull(orderDomain)) {
            return ResponseEntity.status(SkillUpCommon.BAD_REQUEST).body(null);
        }
        // mock 获取异步支付结果
        CompletableFuture<OrderDomain> future = CompletableFuture.supplyAsync(() -> this.asyncGetPaymentResult(orderDomain.getOrderNumber()));
        OrderDomain orderDomainAfterPayment = future.get();
        OrderStatus orderStatusAfterPayment = orderDomainAfterPayment.getOrderStatus();
        if (orderStatusAfterPayment.equals(OrderStatus.PAID)) {
            return ResponseEntity.status(SkillUpCommon.SUCCESS).body(toOutDto(orderDomainAfterPayment));
        }
        if (orderStatusAfterPayment.equals(OrderStatus.CREATED)) {
            return ResponseEntity.status(SkillUpCommon.INTERNAL_ERROR).body(toOutDto(orderDomainAfterPayment));
        }
        if (orderStatusAfterPayment.equals(OrderStatus.OVERTIME)) {
            return ResponseEntity.status(SkillUpCommon.BAD_REQUEST).body(toOutDto(orderDomainAfterPayment));
        } else {
            return ResponseEntity.status(SkillUpCommon.INTERNAL_ERROR).body(toOutDto(orderDomainAfterPayment));
        }
    }

    private OrderDomain toDomain(OrderInDto orderInDto) {
        return OrderDomain.builder()
                .orderNumber(snowflake.nextId())
                .orderStatus(OrderStatus.READY)
                .orderAmount(orderInDto.getOrderAmount())
                .promotionId(orderInDto.getPromotionId())
                .promotionName(orderInDto.getPromotionName())
                .userId(orderInDto.getUserId())
                .build();
    }

    private OrderOutDto toOutDto(OrderDomain orderDomain) {
        return OrderOutDto.builder()
                .orderNumber(String.valueOf(orderDomain.getOrderNumber()))
                .orderStatus(orderDomain.getOrderStatus().code)
                .promotionId(orderDomain.getPromotionId())
                .promotionName(orderDomain.getPromotionName())
                .userId(orderDomain.getUserId())
                .orderAmount(orderDomain.getOrderAmount())
                .createTime(orderDomain.getCreateTime())
                .payTime(orderDomain.getPayTime())
                .build();
    }

    private OrderDomain asyncGetPaymentResult(Long orderNumber) {
        try {
            Thread.sleep(2000);
            return orderService.getOrderById(orderNumber);
        } catch (InterruptedException e) {
            return null;
        }
    }
}
