package com.skillup.infrastructure.mybatis;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("`promotion`")
public class Promotion implements Serializable {
    @TableId
    private String promotionId;

    private String promotionName;

    private String commodityId;

    private Integer originalPrice;

    private Integer promotionPrice;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @TableField("status")
    private Integer status;

    private Long totalStock;

    private Long availableStock;

    private Long lockStock;

    private String imageUrl;

    public void setPromotionId(String promotionId) {
        this.promotionId = promotionId;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public void setCommodityId(String commodityId) {
        this.commodityId = commodityId;
    }

    public void setOriginalPrice(Integer originalPrice) {
        this.originalPrice = originalPrice;
    }

    public void setPromotionPrice(Integer promotionPrice) {
        this.promotionPrice = promotionPrice;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setTotalStock(Long totalStock) {
        this.totalStock = totalStock;
    }

    public void setAvailableStock(Long availableStock) {
        this.availableStock = availableStock;
    }

    public void setLockStock(Long lockStock) {
        this.lockStock = lockStock;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPromotionId() {
        return this.promotionId;
    }

    public String getPromotionName() {
        return this.promotionName;
    }

    public String getCommodityId() {
        return this.commodityId;
    }

    public Integer getOriginalPrice() {
        return this.originalPrice;
    }

    public Integer getPromotionPrice() {
        return this.promotionPrice;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public Integer getStatus() {
        return this.status;
    }

    public Long getTotalStock() {
        return this.totalStock;
    }

    public Long getAvailableStock() {
        return this.availableStock;
    }

    public Long getLockStock() {
        return this.lockStock;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

}
