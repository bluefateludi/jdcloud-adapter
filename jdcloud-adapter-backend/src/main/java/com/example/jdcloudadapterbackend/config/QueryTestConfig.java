package com.example.jdcloudadapterbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 题目3：数据查询测试表配置
 * 用于题目3的分页查询功能
 */
@Data
@Component
@ConfigurationProperties(prefix = "jiandaoyun.query")
public class QueryTestConfig {

    /** 应用ID（题目3专用） */
    private String appId;

    /** 表单ID（子表单entry_id） */
    private String formId;
}
