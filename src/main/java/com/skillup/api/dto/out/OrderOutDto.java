package com.skillup.api.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderOutDto {
    private String orderNumber;
    private Integer orderStatus;
    private String promotionId;
    private String promotionName;
    private String userId;
    private Integer orderAmount;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
}
