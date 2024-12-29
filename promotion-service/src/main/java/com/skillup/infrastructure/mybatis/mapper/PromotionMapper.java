package com.skillup.infrastructure.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.skillup.infrastructure.mybatis.Promotion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PromotionMapper extends BaseMapper<Promotion> {
    @Update("UPDATE promotion " +
            "SET available_stock = available_stock - 1, lock_stock = lock_stock + 1 " +
            "WHERE promotion_id = #{promotionId} AND available_stock > 0")
    int lockPromotionStock(@Param("promotionId") String promotionId);

    @Update("UPDATE  promotion " +
            "SET lock_stock = lock_stock - 1 " +
            "WHERE promotion_id = #{promotionId} AND lock_stock > 0")
    int deductPromotionStock(@Param("promotionId") String promotionId);

    @Update("UPDATE promotion " +
            "SET available_stock = available_stock + 1, lock_stock = lock_stock - 1 " +
            "WHERE promotion_id = #{promotionId} AND lock_stock > 0")
    int revertPromotionStock(@Param("promotionId") String promotionId);
}
