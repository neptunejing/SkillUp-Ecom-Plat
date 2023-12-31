package com.skillup.api;

import com.skillup.api.dto.in.OrderInDto;
import com.skillup.api.dto.out.OrderOutDto;
import com.skillup.api.util.SkillUpCommon;
import com.skillup.api.util.Snowflake;
import com.skillup.application.OrderApplication;
import com.skillup.domain.order.OrderDomain;
import com.skillup.domain.order.OrderService;
import com.skillup.domain.order.util.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
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
    public ResponseEntity<OrderOutDto> payBuyNowOrder(@RequestBody OrderInDto orderInDto) {
        return null;

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
}
