package com.example.jdcloudadapterbackend.model.request;

import lombok.Data;
import java.util.List;

/**
 * 简道云数据请求
 */
@Data
public class JiandaoyunDataRequest {

    /**
     * 应用ID
     */
    private String app_id;

    /**
     * 表单ID
     */
    private String entry_id;

    /**
     * 事务ID
     */
    private String transaction_id;

    /**
     * 数据内容
     */
    private DataContent data;

    @Data
    public static class DataContent {
        /**
         * 用户信息子表单
         */
        private UserInfoValue user_info;
    }

    /**
     * 用户信息子表单值
     */
    @Data
    public static class UserInfoValue {
        private List<UserInfoItem> value;

        public UserInfoValue() {}

        public UserInfoValue(List<UserInfoItem> value) {
            this.value = value;
        }
    }
}