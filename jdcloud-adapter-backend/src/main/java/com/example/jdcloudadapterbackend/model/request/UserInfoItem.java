package com.example.jdcloudadapterbackend.model.request;

import lombok.Data;

/**
 * 简道云子表单用户信息项
 */
@Data
public class UserInfoItem {

    /**
     * 用户ID
     */
    private UserIdValue user_id;

    /**
     * 用户名
     */
    private StringValue user_name;

    /**
     * 手机号
     */
    private StringValue phone;

    /**
     * 状态
     */
    private StringValue status;

    /**
     * 带value的字符串值
     */
    @Data
    public static class StringValue {
        private String value;

        public StringValue() {}

        public StringValue(String value) {
            this.value = value;
        }
    }

    /**
     * 带value的数字值
     */
    @Data
    public static class UserIdValue {
        private Long value;

        public UserIdValue() {}

        public UserIdValue(Long value) {
            this.value = value;
        }
    }
}