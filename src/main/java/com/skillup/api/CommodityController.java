package com.skillup.api;

import com.skillup.api.dto.in.CommodityInDto;
import com.skillup.api.dto.out.CommodityOutDto;
import com.skillup.api.util.SkillUpCommon;
import com.skillup.api.util.SkillUpResponse;
import com.skillup.domain.commodity.CommodityDomain;
import com.skillup.domain.commodity.CommodityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/commodity")
public class CommodityController {
    @Autowired
    CommodityService commodityService;

    @PostMapping()
    public ResponseEntity<SkillUpResponse> createCommodity(@RequestBody CommodityInDto commodityInDto) {
        try {
            CommodityDomain commodityDomain = commodityService.createCommodity(toDomain(commodityInDto));
            return ResponseEntity.status(SkillUpCommon.SUCCESS).body(SkillUpResponse.builder().result(toOutDto(commodityDomain)).build());
        } catch (Exception e) {
            return ResponseEntity.status(SkillUpCommon.BAD_REQUEST).body(SkillUpResponse.builder().build());
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<SkillUpResponse> getCommodityById(@PathVariable("id") String id) {
        CommodityDomain commodityDomain = commodityService.getCommodityById(id);
        if (!Objects.isNull(commodityDomain)) {
            return ResponseEntity.status(SkillUpCommon.SUCCESS).body(SkillUpResponse.builder().result(toOutDto(commodityDomain)).build());
        }
        return ResponseEntity.status(SkillUpCommon.BAD_REQUEST).body(SkillUpResponse.builder().build());
    }

    private CommodityDomain toDomain(CommodityInDto commodityInDto) {
        return CommodityDomain.builder()
                .commodityId(UUID.randomUUID().toString())
                .commodityName(commodityInDto.getCommodityName())
                .description(commodityInDto.getDescription())
                .price(commodityInDto.getPrice())
                .imageURL(commodityInDto.getImageURL())
                .build();
    }

    private CommodityOutDto toOutDto(CommodityDomain commodityDomain) {
        return CommodityOutDto.builder()
                .commodityId(commodityDomain.getCommodityId())
                .commodityName(commodityDomain.getCommodityName())
                .description(commodityDomain.getDescription())
                .price(commodityDomain.getPrice())
                .imageURL(commodityDomain.getImageURL())
                .build();
    }


}
