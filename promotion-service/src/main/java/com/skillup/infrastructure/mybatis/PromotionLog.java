package com.skillup.infrastructure.mybatis;

import java.io.Serializable;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("`promotion_log`")
public class PromotionLog implements Serializable {
    private Long orderNumber;

    private String operationName;

    private String userId;

    private String promotionId;

    private LocalDateTime createTime;

    private Integer status;

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPromotionId(String promotionId) {
        this.promotionId = promotionId;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getOrderNumber() {
        return this.orderNumber;
    }

    public String getOperationName() {
        return this.operationName;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getPromotionId() {
        return this.promotionId;
    }

    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    public Integer getStatus() {
        return this.status;
    }

}
