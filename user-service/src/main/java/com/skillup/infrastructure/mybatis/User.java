package com.skillup.infrastructure.mybatis;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("`user`")
public class User implements Serializable {
    @TableId()
    private String userId;

    @TableField("user_name")
    private String userName;

    private String password;

    public void setUserId(String userId){
    this.userId = userId;
    }

    public void setUserName(String userName){
    this.userName = userName;
    }

    public void setPassword(String password){
    this.password = password;
    }

    public String getUserName(){
    return this.userName;
    }

    public String getPassword(){
    return this.password;
    }

}
