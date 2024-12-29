package com.skillup.infrastructure.mybatis;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("`commodity`")
public class Commodity implements Serializable {
    @TableId()
    private String commodityId;

    private String commodityName;

    private String description;

    private Integer price;

    private String imageUrl;

    public void setCommodityId(String commodityId){ this.commodityId = commodityId; }

    public void setCommodityName(String commodityName){ this.commodityName = commodityName; }

    public void setDescription(String description){ this.description = description; }

    public void setPrice(Integer price){ this.price = price; }

    public void setImageUrl(String imageUrl){ this.imageUrl = imageUrl; }

    public String getCommodityId(){ return this.commodityId; }

    public String getCommodityName(){ return this.commodityName; }

    public String getDescription(){ return this.description; }

    public Integer getPrice(){ return this.price; }

    public String getImageUrl(){ return this.imageUrl; }
}
