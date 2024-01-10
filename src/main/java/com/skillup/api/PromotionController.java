package com.skillup.api;

import com.skillup.api.dto.in.PromotionInDto;
import com.skillup.api.mapper.PromotionMapper;
import com.skillup.api.dto.out.PromotionOutDto;
import com.skillup.api.util.SkillUpCommon;
import com.skillup.domain.promotion.PromotionDomain;
import com.skillup.domain.promotion.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/promotion")
public class PromotionController {
    @Autowired
    PromotionService promotionService;

    @PostMapping()
    public ResponseEntity<PromotionOutDto> createPromotion(@RequestBody PromotionInDto promotionInDto) {
        try {
            PromotionDomain promotionDomain = promotionService.createPromotion(PromotionMapper.INSTANCE.toDomain(promotionInDto));
            return ResponseEntity.status(SkillUpCommon.SUCCESS).body(PromotionMapper.INSTANCE.toOutDto(promotionDomain));
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
        return ResponseEntity.status(SkillUpCommon.SUCCESS).body(PromotionMapper.INSTANCE.toOutDto(promotionDomain));
    }

    @GetMapping("/status/{status}")
    public List<PromotionOutDto> getPromotionByStatus(@PathVariable("status") int status) {
        List<PromotionDomain> promotionDomainList = promotionService.getPromotionByStatus(status);
        return promotionDomainList.stream().map(PromotionMapper.INSTANCE::toOutDto).collect(Collectors.toList());
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
        PromotionDomain promotionDomain = promotionService.getPromotionById(promotionId);
        if (Objects.isNull(promotionDomain)) {
            return ResponseEntity.status(SkillUpCommon.BAD_REQUEST).body(false);
        }
        boolean isDeducted = promotionService.deductPromotionStock(promotionId);
        if (isDeducted) {
            return ResponseEntity.status(SkillUpCommon.SUCCESS).body(true);
        }
        return ResponseEntity.status(SkillUpCommon.SUCCESS).body(false);

    }

    @PostMapping("revert/id/{id}")
    public ResponseEntity<Boolean> revertPromotionStock(@PathVariable("id") String promotionId) {
        PromotionDomain promotionDomain = promotionService.getPromotionById(promotionId);
        if (Objects.isNull(promotionDomain)) {
            return ResponseEntity.status(SkillUpCommon.BAD_REQUEST).body(false);
        }
        boolean isReverted = promotionService.revertPromotionStock(promotionId);
        if (isReverted) {
            return ResponseEntity.status(SkillUpCommon.SUCCESS).body(true);
        }
        return ResponseEntity.status(SkillUpCommon.SUCCESS).body(false);
    }
}
