package com.skillup.infrastructure.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skillup.domain.promotionStockLog.PromotionStockLogDomain;
import com.skillup.domain.promotionStockLog.PromotionStockLogRepo;
import com.skillup.domain.promotionStockLog.util.OperationName;
import com.skillup.domain.promotionStockLog.util.OperationStatus;
import com.skillup.infrastructure.mybatis.mapper.PromotionLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Repository
public class batisPromotionLogRepo implements PromotionStockLogRepo {
    @Autowired
    private PromotionLogMapper promotionLogMapper;

    @Override
    @Transactional
    public void createPromotionStockLog(PromotionStockLogDomain promotionStockLogDomain) {
        promotionLogMapper.insert(toPromotionLog(promotionStockLogDomain));
    }

    @Override
    @Transactional
    public void updatePromotionStockLog(PromotionStockLogDomain promotionStockLogDomain) {
        QueryWrapper<PromotionLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_number", promotionStockLogDomain.getOrderNumber())
                .eq("operation_name", promotionStockLogDomain.getOperationName().name());

        promotionLogMapper.update(toPromotionLog(promotionStockLogDomain), queryWrapper);
    }

    @Override
    @Transactional
    public PromotionStockLogDomain getLogByOrderIdAndOperation(Long orderId, String operationName) {
        QueryWrapper<PromotionLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_number", orderId)
                .eq("operation_name", operationName)
                .last("FOR UPDATE");

        PromotionLog promotionLog = promotionLogMapper.selectOne(queryWrapper);
        return Objects.isNull(promotionLog) ? null : toDomain(promotionLog);
    }

    private PromotionLog toPromotionLog(PromotionStockLogDomain promotionStockLogDomain) {
        return PromotionLog.builder()
                .orderNumber(promotionStockLogDomain.getOrderNumber())
                .userId(promotionStockLogDomain.getUserId())
                .promotionId(promotionStockLogDomain.getPromotionId())
                .operationName(promotionStockLogDomain.getOperationName().toString())
                .createTime(promotionStockLogDomain.getCreateTime())
                .status(promotionStockLogDomain.getStatus().code)
                .build();
    }

    private PromotionStockLogDomain toDomain(PromotionLog promotionLog) {
        return PromotionStockLogDomain.builder()
                .orderNumber(promotionLog.getOrderNumber())
                .userId(promotionLog.getUserId())
                .promotionId(promotionLog.getPromotionId())
                .operationName(OperationName.valueOf(promotionLog.getOperationName()))
                .createTime(promotionLog.getCreateTime())
                .status(OperationStatus.CACHE.get(promotionLog.getStatus()))
                .build();
    }
}
