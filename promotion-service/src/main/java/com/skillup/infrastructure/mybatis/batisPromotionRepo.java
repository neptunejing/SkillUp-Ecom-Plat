package com.skillup.infrastructure.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skillup.domain.promotion.PromotionDomain;
import com.skillup.domain.promotion.PromotionRepository;
import com.skillup.domain.promotion.stockStrategy.StockOperation;
import com.skillup.infrastructure.mybatis.mapper.PromotionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository(value = "optimisticBatis")
@Slf4j
public class batisPromotionRepo implements PromotionRepository, StockOperation {
    @Autowired
    PromotionMapper promotionMapper;

    @Override
    @Transactional
    public void createPromotion(PromotionDomain promotionDomain) {
        promotionMapper.insert(toPromotion(promotionDomain));
    }

    @Override
    @Transactional
    public PromotionDomain getPromotionById(String promotionId) {
        Promotion promotion = promotionMapper.selectById(promotionId);
        if (Objects.isNull(promotion)) {
            return null;
        }
        return toDomain(promotion);
    }

    @Override
    public List<PromotionDomain> getPromotionByStatus(int status) {
        QueryWrapper<Promotion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", status);
        List<Promotion> promotionList = promotionMapper.selectList(queryWrapper);
        return promotionList.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void updatePromotionStock(PromotionDomain promotionDomain) {
        Promotion promotion = toPromotion(promotionDomain);
        promotionMapper.updateById(promotion);

    }

    @Override
    @Transactional
    public boolean lockPromotionStock(String promotionId) {
        log.info("----- Optimistic Strategy: Lock -----");
        int isLocked = promotionMapper.lockPromotionStock(promotionId);
        return isLocked == 1;
    }

    @Override
    public boolean deductPromotionStock(String promotionId) {
        log.info("----- Optimistic Strategy: Deduct -----");
        int isDeducted = promotionMapper.deductPromotionStock(promotionId);
        return isDeducted == 1;
    }

    @Override
    @Transactional
    public boolean revertPromotionStock(String promotionId) {
        log.info("----- Optimistic Strategy: Revert -----");
        int isReverted = 0;
        try {
            isReverted = promotionMapper.revertPromotionStock(promotionId);
        } catch (Exception e) {
            log.error("[Batis] Revert DB stock failed, promotionId={}. {}", promotionId, e.getMessage());
        }
        return isReverted == 1;
    }

    private Promotion toPromotion(PromotionDomain promotionDomain) {
        return Promotion.builder()
                .promotionId(promotionDomain.getPromotionId())
                .promotionName(promotionDomain.getPromotionName())
                .commodityId(promotionDomain.getCommodityId())
                .originalPrice(promotionDomain.getOriginalPrice())
                .startTime(promotionDomain.getStartTime())
                .endTime(promotionDomain.getEndTime())
                .status(promotionDomain.getStatus())
                .totalStock(promotionDomain.getTotalStock())
                .availableStock(promotionDomain.getAvailableStock())
                .lockStock(promotionDomain.getLockStock())
                .imageUrl(promotionDomain.getImageURL())
                .build();
    }

    private PromotionDomain toDomain(Promotion promotion) {
        return PromotionDomain.builder()
                .promotionId(promotion.getPromotionId())
                .promotionName(promotion.getPromotionName())
                .commodityId(promotion.getCommodityId())
                .originalPrice(promotion.getOriginalPrice())
                .promotionalPrice(promotion.getPromotionPrice())
                .startTime(promotion.getStartTime())
                .endTime(promotion.getEndTime())
                .status(promotion.getStatus())
                .totalStock(promotion.getTotalStock())
                .availableStock(promotion.getAvailableStock())
                .lockStock(promotion.getLockStock())
                .imageURL(promotion.getImageUrl())
                .build();

    }
}
