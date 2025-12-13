package com.example.jdcloudadapterbackend.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 用户注册请求DTO
 * 题目1：热态调用简道云API
 */
@Data
public class CreateUserRequest {

    /**
     * 用户名：2-20位，数字+字母组成
     */
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{2,20}$", message = "用户名必须为2-20位的数字或字母组合")
    private String username;

    /**
     * 手机号：11位数字
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确，必须为11位数字")
    private String phone;
}
