package com.skillup.infrastructure.repoImpl;

import com.skillup.domain.commodity.CommodityDomain;
import com.skillup.domain.commodity.CommodityRepository;
import com.skillup.infrastructure.jooq.tables.Commodity;
import com.skillup.infrastructure.jooq.tables.records.CommodityRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JooqCommodityRepo implements CommodityRepository {
    @Autowired
    DSLContext dslContext;

    public static final Commodity COMMODITY_T = new Commodity();

    @Override
    public void createCommodity(CommodityDomain commodityDomain) {
        dslContext.executeInsert(toRecord(commodityDomain));
    }

    @Override
    public CommodityDomain getCommodityBy(String id) {
        Optional<CommodityDomain> commodityDomainOptional = dslContext.selectFrom(COMMODITY_T).where(COMMODITY_T.COMMODITY_ID.eq(id)).fetchOptional(this::toDomain);
        return commodityDomainOptional.orElse(null);
    }

    private CommodityRecord toRecord(CommodityDomain commodityDomain) {
        CommodityRecord commodityRecord = new CommodityRecord();
        commodityRecord.setCommodityId(commodityDomain.getCommodityId());
        commodityRecord.setCommodityName(commodityDomain.getCommodityName());
        commodityRecord.setDescription(commodityDomain.getDescription());
        commodityRecord.setPrice(commodityDomain.getPrice());
        commodityRecord.setImageUrl(commodityDomain.getImageURL());
        return commodityRecord;
    }

    private CommodityDomain toDomain(CommodityRecord commodityRecord) {
        return CommodityDomain.builder()
                .commodityId(commodityRecord.getCommodityId())
                .commodityName(commodityRecord.getCommodityName())
                .description(commodityRecord.getDescription())
                .price(commodityRecord.getPrice())
                .imageURL(commodityRecord.getImageUrl())
                .build();
    }
}
