package com.skillup.infrastructure.repoImpl;

import com.skillup.domain.promotion.PromotionDomain;
import com.skillup.domain.promotion.PromotionRepository;
import com.skillup.infrastructure.jooq.tables.Promotion;
import com.skillup.infrastructure.jooq.tables.records.PromotionRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class JooqPromotionRepo implements PromotionRepository {
    @Autowired
    DSLContext dslContext;
    public static final Promotion PROMOTION_T = new Promotion();

    @Override
    public void createPromotion(PromotionDomain promotionDomain) {
        dslContext.executeInsert(toRecord(promotionDomain));
    }

    @Override
    public PromotionDomain getPromotionById(String promotionId) {
        Optional<PromotionDomain> promotionDomainOptional = dslContext.selectFrom(PROMOTION_T).where(PROMOTION_T.PROMOTION_ID.eq(promotionId)).fetchOptional(this::toDomain);
        return promotionDomainOptional.orElse(null);
    }

    @Override
    public List<PromotionDomain> getPromotionByStatus(int status) {
        return dslContext.selectFrom(PROMOTION_T).where(PROMOTION_T.STATUS.eq(status)).fetch(this::toDomain);
    }

    @Override
    public void updatePromotionStock(PromotionDomain promotionDomain) {
        dslContext.executeUpdate(toRecord(promotionDomain));
    }

    @Override
    public boolean lockPromotionStock(String promotionId) {
        return false;
    }


    @Override
    public boolean deductPromotionStock(String promotionId) {
        return false;
    }

    @Override
    public boolean revertPromotionStock(String promotionId) {
        return false;
    }

    private PromotionRecord toRecord(PromotionDomain promotionDomain) {
        PromotionRecord promotionRecord = new PromotionRecord();
        promotionRecord.setPromotionId(promotionDomain.getPromotionId());
        promotionRecord.setPromotionName(promotionDomain.getPromotionName());
        promotionRecord.setCommodityId(promotionDomain.getCommodityId());
        promotionRecord.setOriginalPrice(promotionDomain.getOriginalPrice());
        promotionRecord.setPromotionPrice(promotionDomain.getPromotionalPrice());
        promotionRecord.setStartTime(promotionDomain.getStartTime());
        promotionRecord.setEndTime((promotionDomain.getEndTime()));
        promotionRecord.setStatus(promotionDomain.getStatus());
        promotionRecord.setTotalStock(promotionDomain.getTotalStock());
        promotionRecord.setAvailableStock(promotionDomain.getAvailableStock());
        promotionRecord.setLockStock(promotionDomain.getLockStock());
        promotionRecord.setImageUrl(promotionDomain.getImageURL());
        return promotionRecord;
    }

    private PromotionDomain toDomain(PromotionRecord promotionRecord) {
        return PromotionDomain.builder()
                .promotionId(promotionRecord.getPromotionId())
                .promotionName(promotionRecord.getPromotionName())
                .commodityId(promotionRecord.getCommodityId())
                .startTime(promotionRecord.getStartTime())
                .endTime(promotionRecord.getEndTime())
                .status(promotionRecord.getStatus())
                .totalStock(promotionRecord.getTotalStock())
                .availableStock(promotionRecord.getAvailableStock())
                .lockStock(promotionRecord.getLockStock())
                .imageURL(promotionRecord.getImageUrl())
                .build();
    }
}
