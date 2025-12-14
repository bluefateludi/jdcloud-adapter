package com.example.jdcloudadapterbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 简道云Widget ID配置
 * 用于管理简道云表单字段的widget_id配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "jiandaoyun.widget")
public class JiandaoyunWidgetConfig {

    /**
     * 用户ID字段的widget_id
     * 别名：user_id
     */
    private String user_id;

    /**
     * 用户名字段的widget_id
     * 别名：user_name
     */
    private String user_name;

    /**
     * 手机号字段的widget_id
     * 别名：phone
     */
    private String phone;

    /**
     * 状态字段的widget_id
     * 别名：status
     */
    private String status;
}