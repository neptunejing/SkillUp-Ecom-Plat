package com.skillup.api.dto.mapper;

import com.skillup.api.dto.in.PromotionInDto;
import com.skillup.api.dto.out.PromotionOutDto;
import com.skillup.domain.promotion.PromotionDomain;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PromotionMapper {
    PromotionMapper INSTANCE = Mappers.getMapper(PromotionMapper.class);

    @Mapping(target = "promotionId", expression = "java(java.util.UUID.randomUUID().toString())")
    PromotionDomain toDomain(PromotionInDto promotionInDto);

    PromotionOutDto toOutDto(PromotionDomain promotionDomain);
}
