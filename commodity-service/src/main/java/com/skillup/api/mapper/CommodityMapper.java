package com.skillup.api.mapper;

import com.skillup.api.dto.in.CommodityInDto;
import com.skillup.api.dto.out.CommodityOutDto;
import com.skillup.domain.commodity.CommodityDomain;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CommodityMapper {
    CommodityMapper INSTANCE = Mappers.getMapper(CommodityMapper.class);

    @Mapping(target = "commodityId", expression = "java(java.util.UUID.randomUUID().toString())")
    CommodityDomain toDomain(CommodityInDto commodityInDto);

    CommodityOutDto toOutDto(CommodityDomain commodityDomain);
}
