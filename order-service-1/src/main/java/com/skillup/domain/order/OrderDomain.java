package com.skillup.domain.order;

import com.skillup.domain.order.util.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDomain {
    private long orderNumber;
    private OrderStatus orderStatus;
    private Integer orderAmount;
    private String promotionId;
    private String promotionName;
    private String userId;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
}
