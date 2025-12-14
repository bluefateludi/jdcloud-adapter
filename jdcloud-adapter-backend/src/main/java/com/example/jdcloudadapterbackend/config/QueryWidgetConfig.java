package com.example.jdcloudadapterbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 题目3：Widget ID配置
 * 对应子表单内的字段
 */
@Data
@Component
@ConfigurationProperties(prefix = "jiandaoyun.widget.query")
public class QueryWidgetConfig {

    /** 子表单父级（array类型）_widget_1765719114391 */
    private String testInfo;

    /** 用户名字段 _widget_1765719114391._widget_1765719114393 */
    private String name;

    /** 手机号字段 _widget_1765719114391._widget_1765719114402 */
    private String phone;

    /** 部门字段 _widget_1765719114391._widget_1765719114398 */
    private String department;

    /** 状态字段 _widget_1765719114391._widget_1765719114400 */
    private String status;
}
