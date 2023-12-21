package com.skillup.domain.commodity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommodityDomain {
    private String commodityId;
    private String commodityName;
    private String description;
    private int price;
    private String imageURL;
}
