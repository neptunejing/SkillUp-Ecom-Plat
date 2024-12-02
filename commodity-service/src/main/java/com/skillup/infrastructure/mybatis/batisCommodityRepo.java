package com.skillup.infrastructure.mybatis;

import com.skillup.domain.commodity.CommodityDomain;
import com.skillup.domain.commodity.CommodityRepository;
import com.skillup.infrastructure.mybatis.mapper.CommodityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Repository
public class batisCommodityRepo implements CommodityRepository {
    @Autowired
    CommodityMapper commodityMapper;

    @Override
    @Transactional
    public void createCommodity(CommodityDomain commodityDomain) {
        commodityMapper.insert(toCommodity(commodityDomain));
    }

    @Override
    @Transactional
    public CommodityDomain getCommodityBy(String id) {
        Commodity commodity = commodityMapper.selectById(id);
        if (Objects.isNull(commodity)) {
            return null;
        }
        return toDomain(commodity);
    }

    private Commodity toCommodity(CommodityDomain commodityDomain) {
        return Commodity.builder()
                .commodityId(commodityDomain.getCommodityId())
                .commodityName(commodityDomain.getCommodityName())
                .description(commodityDomain.getDescription())
                .price(commodityDomain.getPrice())
                .imageUrl(commodityDomain.getImageURL())
                .build();
    }

    private CommodityDomain toDomain(Commodity commodity) {
        return CommodityDomain.builder()
                .commodityId(commodity.getCommodityId())
                .commodityName(commodity.getCommodityName())
                .description(commodity.getDescription())
                .price(commodity.getPrice())
                .imageURL(commodity.getImageUrl())
                .build();
    }
}
