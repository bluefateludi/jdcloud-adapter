package com.example.jdcloudadapterbackend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户基础信息实体
 * 题目1：用于存储用户注册信息
 */
@Data
@TableName("user_info")
public class UserEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名：2-20位，数字+字母
     */
    private String username;

    /**
     * 手机号：11位数字
     */
    private String phone;

    /**
     * 状态：1=启用，0=停用
     */
    private Integer status;

}
