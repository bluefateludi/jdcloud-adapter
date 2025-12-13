-- 题目1：用户基础表
-- 用途：本地存储用户信息，用于唯一性校验和并发控制

CREATE DATABASE IF NOT EXISTS jdcloud_adapter DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE jdcloud_adapter;

-- 用户基础表（只包含题目要求的三个基础字段）
CREATE TABLE IF NOT EXISTS user_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(20) NOT NULL COMMENT '用户名：2-20位，数字+字母',
    phone VARCHAR(11) NOT NULL COMMENT '手机号：11位数字',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1=启用，0=停用',
    UNIQUE KEY uk_username (username) COMMENT '用户名唯一索引（并发控制）',
    UNIQUE KEY uk_phone (phone) COMMENT '手机号唯一索引（并发控制）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户基础表';
