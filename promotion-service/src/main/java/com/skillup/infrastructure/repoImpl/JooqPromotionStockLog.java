package com.skillup.infrastructure.repoImpl;

import com.skillup.domain.promotionStockLog.PromotionStockLogDomain;
import com.skillup.domain.promotionStockLog.PromotionStockLogRepo;
import com.skillup.domain.promotionStockLog.util.OperationName;
import com.skillup.domain.promotionStockLog.util.OperationStatus;
import com.skillup.infrastructure.jooq.tables.PromotionLog;
import com.skillup.infrastructure.jooq.tables.records.PromotionLogRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JooqPromotionStockLog implements PromotionStockLogRepo {
    @Autowired
    DSLContext dslContext;

    public static final PromotionLog PROMOTION_LOG_T = new PromotionLog();

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createPromotionStockLog(PromotionStockLogDomain promotionStockLogDomain) {
        dslContext.executeInsert(toRecord(promotionStockLogDomain));
    }

    @Override
    public void updatePromotionStockLog(PromotionStockLogDomain promotionStockLogDomain) {
        dslContext.executeUpdate(toRecord(promotionStockLogDomain));
    }

    @Override
    public PromotionStockLogDomain getLogByOrderIdAndOperation(Long orderId, String operationName) {
        return dslContext.selectFrom(PROMOTION_LOG_T).where(PROMOTION_LOG_T.ORDER_NUMBER.eq(orderId).and(PROMOTION_LOG_T.OPERATION_NAME.eq(operationName)))
                .forUpdate().fetchOptional(this::toDomain).orElse(null);
    }

    private PromotionLogRecord toRecord(PromotionStockLogDomain promotionStockLogDomain) {
        return new PromotionLogRecord(
                promotionStockLogDomain.getOrderNumber(),
                promotionStockLogDomain.getUserId(),
                promotionStockLogDomain.getPromotionId(),
                promotionStockLogDomain.getOperationName().toString(),
                promotionStockLogDomain.getCreateTime(),
                promotionStockLogDomain.getStatus().code
        );
    }

    private PromotionStockLogDomain toDomain(PromotionLogRecord promotionLogRecord) {
        return PromotionStockLogDomain.builder()
                .orderNumber(promotionLogRecord.getOrderNumber())
                .userId(promotionLogRecord.getUserId())
                .promotionId(promotionLogRecord.getPromotionId())
                .operationName(OperationName.valueOf(promotionLogRecord.getOperationName()))
                .createTime(promotionLogRecord.getCreateTime())
                .status(OperationStatus.CACHE.get(promotionLogRecord.getStatus()))
                .build();
    }
}
