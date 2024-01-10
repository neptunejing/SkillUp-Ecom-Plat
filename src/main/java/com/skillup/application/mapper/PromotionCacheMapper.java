package com.skillup.application.mapper;

import com.skillup.domain.promotion.PromotionCacheDomain;
import com.skillup.domain.promotion.PromotionDomain;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PromotionCacheMapper {
    PromotionCacheMapper INSTANCE = Mappers.getMapper(PromotionCacheMapper.class);

    PromotionCacheDomain toCache(PromotionDomain promotionDomain);
}
