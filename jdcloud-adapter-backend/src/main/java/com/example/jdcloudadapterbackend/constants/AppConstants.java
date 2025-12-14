package com.example.jdcloudadapterbackend.constants;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 应用业务常量配置
 * 用于管理应用程序中的业务常量
 */
@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppConstants {

    /**
     * 用户相关配置
     */
    private User user = new User();

    /**
     * 错误消息配置
     */
    private Error error = new Error();

    /**
     * 事务配置
     */
    private Transaction transaction = new Transaction();

    @Data
    public static class User {
        /**
         * 用户状态配置
         */
        private Status status = new Status();

        @Data
        public static class Status {
            /**
             * 启用状态值
             */
            private String enabled;

            /**
             * 禁用状态值
             */
            private String disabled;
        }
    }

    @Data
    public static class Error {
        /**
         * 用户名已存在错误消息
         */
        private String usernameExists;

        /**
         * 手机号已存在错误消息
         */
        private String phoneExists;
    }

    @Data
    public static class Transaction {
        /**
         * 事务ID前缀
         */
        private String prefix;
    }
}