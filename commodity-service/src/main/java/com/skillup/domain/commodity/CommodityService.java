package com.skillup.domain.commodity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class CommodityService {
    @Autowired
    @Qualifier("batisCommodityRepo")
    CommodityRepository commodityRepository;

    public CommodityDomain createCommodity(CommodityDomain commodityDomain) {
        commodityRepository.createCommodity(commodityDomain);
        return commodityDomain;
    }

    public CommodityDomain getCommodityById(String id) {
        return commodityRepository.getCommodityBy(id);
    }

}
