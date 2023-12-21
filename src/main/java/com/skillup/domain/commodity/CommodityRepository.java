package com.skillup.domain.commodity;

public interface CommodityRepository {
    void createCommodity(CommodityDomain commodityDomain);

    CommodityDomain getCommodityBy(String id);
}
