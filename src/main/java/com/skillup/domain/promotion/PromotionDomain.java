package com.skillup.domain.promotion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionDomain {
    private String promotionId;
    private String promotionName;
    private String commodityId;
    private int originalPrice;
    private int promotionalPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int status;
    private long totalStock;
    private long availableStock;
    private long lockStock;
    private String imageURL;
}
