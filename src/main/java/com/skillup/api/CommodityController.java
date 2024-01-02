package com.skillup.api;

import com.skillup.api.dto.in.CommodityInDto;
import com.skillup.api.dto.mapper.CommodityMapper;
import com.skillup.api.util.SkillUpCommon;
import com.skillup.api.util.SkillUpResponse;
import com.skillup.domain.commodity.CommodityDomain;
import com.skillup.domain.commodity.CommodityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/commodity")
public class CommodityController {
    @Autowired
    CommodityService commodityService;

    @PostMapping()
    public ResponseEntity<SkillUpResponse> createCommodity(@RequestBody CommodityInDto commodityInDto) {
        try {
            CommodityDomain commodityDomain = commodityService.createCommodity(CommodityMapper.INSTANCE.toDomain(commodityInDto));
            return ResponseEntity.status(SkillUpCommon.SUCCESS).body(SkillUpResponse.builder().result(CommodityMapper.INSTANCE.toOutDto(commodityDomain)).build());
        } catch (Exception e) {
            return ResponseEntity.status(SkillUpCommon.BAD_REQUEST).body(SkillUpResponse.builder().build());
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<SkillUpResponse> getCommodityById(@PathVariable("id") String id) {
        CommodityDomain commodityDomain = commodityService.getCommodityById(id);
        if (!Objects.isNull(commodityDomain)) {
            return ResponseEntity.status(SkillUpCommon.SUCCESS).body(SkillUpResponse.builder().result(CommodityMapper.INSTANCE.toOutDto(commodityDomain)).build());
        }
        return ResponseEntity.status(SkillUpCommon.BAD_REQUEST).body(SkillUpResponse.builder().build());
    }
}
