package com.example.jdcloudadapterbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 简道云API配置类
 * 从application.properties读取配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "jiandaoyun.api")
public class JiandaoyunApiConfig {

    /**
     * API基础URL
     */
    private String baseUrl;

    /**
     * AppKey
     */
    private String appKey;

    /**
     * AppId
     */
    private String appId;

    /**
     * AppSecret
     */
    private String appSecret;

    /**
     * 用户基础表表单ID（题目1）
     */
    private String formIdUserBase;

    /**
     * API超时时间（秒）
     */
    private Integer timeout;

    /**
     * 通讯录API - 创建成员
     */
    private String memberCreate;

    /**
     * 通讯录API - 删除成员
     */
    private String memberDelete;

    /**
     * 数据API - 创建数据
     */
    private String dataCreate;

    /**
     * 数据API - 查询数据
     */
    private String dataRetrieve;
}
