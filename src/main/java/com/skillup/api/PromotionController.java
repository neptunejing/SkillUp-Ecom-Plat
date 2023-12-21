package com.skillup.api;

import com.skillup.api.dto.in.PromotionInDto;
import com.skillup.api.dto.out.PromotionOutDto;
import com.skillup.api.util.SkillUpCommon;
import com.skillup.domain.promotion.PromotionDomain;
import com.skillup.domain.promotion.PromotionRepository;
import com.skillup.domain.promotion.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/promotion")
public class PromotionController {
    @Autowired
    PromotionService promotionService;

    @PostMapping()
    public ResponseEntity<PromotionOutDto> createPromotion(@RequestBody PromotionInDto promotionInDto) {
        try {
            PromotionDomain promotionDomain = promotionService.createPromotion(toDomain(promotionInDto));
            return ResponseEntity.status(SkillUpCommon.SUCCESS).body(toDto(promotionDomain));
        } catch (Exception e) {
            return ResponseEntity.status(SkillUpCommon.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<PromotionOutDto> getPromotionById(@PathVariable("id") String promotionId) {
        PromotionDomain promotionDomain = promotionService.getPromotionById(promotionId);
        if (Objects.isNull(promotionDomain)) {
            return ResponseEntity.status(SkillUpCommon.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(SkillUpCommon.SUCCESS).body(toDto(promotionDomain));
    }

    @GetMapping("/status/{status}")
    public List<PromotionOutDto> getPromotionByStatus(@PathVariable("status") int status) {
        List<PromotionDomain> promotionDomainList = promotionService.getPromotionByStatus(status);
        return promotionDomainList.stream().map(this::toDto).collect(Collectors.toList());
    }

    @PostMapping("/lock/id/{id}")
    public ResponseEntity<Boolean> lockPromotionStock(@PathVariable("id") String promotionId) {
        PromotionDomain promotionDomain = promotionService.getPromotionById(promotionId);
        if (Objects.isNull(promotionDomain)) {
            return ResponseEntity.status(SkillUpCommon.BAD_REQUEST).body(false);
        }
        boolean isLocked = promotionService.lockPromotionStock(promotionId);
        if (isLocked) {
            return ResponseEntity.status(SkillUpCommon.SUCCESS).body(true);
        }
        return ResponseEntity.status(SkillUpCommon.SUCCESS).body(false);
    }

    @PostMapping("/deduct/id/{id}")
    public ResponseEntity<Boolean> deductPromotionStock(@PathVariable("id") String promotionId) {
        return null;
    }

    @PostMapping("revert/id/{id}")
    public ResponseEntity<Boolean> revertPromotionStock(@PathVariable("id") String promotionId) {
        return null;
    }

    private PromotionDomain toDomain(PromotionInDto promotionInDto) {
        return PromotionDomain.builder()
                .promotionId(UUID.randomUUID().toString())
                .promotionName(promotionInDto.getPromotionName())
                .commodityId(promotionInDto.getCommodityId())
                .originalPrice(promotionInDto.getOriginalPrice())
                .promotionalPrice(promotionInDto.getPromotionalPrice())
                .startTime(promotionInDto.getStartTime())
                .endTime(promotionInDto.getEndTime())
                .totalStock(promotionInDto.getTotalStock())
                .availableStock(promotionInDto.getAvailableStock())
                .lockStock(promotionInDto.getLockStock())
                .imageURL(promotionInDto.getImageURL())
                .build();
    }

    private PromotionOutDto toDto(PromotionDomain promotionDomain) {
        return PromotionOutDto.builder()
                .promotionId(promotionDomain.getPromotionId())
                .promotionName(promotionDomain.getPromotionName())
                .commodityId(promotionDomain.getCommodityId())
                .originalPrice(promotionDomain.getOriginalPrice())
                .promotionalPrice(promotionDomain.getPromotionalPrice())
                .startTime(promotionDomain.getStartTime())
                .endTime(promotionDomain.getEndTime())
                .totalStock(promotionDomain.getOriginalPrice())
                .availableStock(promotionDomain.getAvailableStock())
                .lockStock(promotionDomain.getLockStock())
                .imageURL(promotionDomain.getImageURL())
                .build();

    }

}
