package com.example.jdcloudadapterbackend.controller;

import com.example.jdcloudadapterbackend.model.request.CreateUserRequest;
import com.example.jdcloudadapterbackend.model.response.ApiResponse;
import com.example.jdcloudadapterbackend.model.entity.UserEntity;
import com.example.jdcloudadapterbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户控制器
 * 题目1：用户注册接口
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册接口
     * POST /api/user/register
     *
     * @param request 用户注册请求
     * @return 注册结果
     */
    @PostMapping("/register")
    public ApiResponse<UserEntity> register(@Valid @RequestBody CreateUserRequest request) {
        try {
            log.info("收到用户注册请求: username={}, phone={}", request.getUsername(), request.getPhone());

            // 调用Service创建用户
            UserEntity user = userService.createUser(request);

            return ApiResponse.success(user);

        } catch (Exception e) {
            log.error("用户注册失败", e);
            // 返回友好的错误信息
            String errorMessage = e.getMessage();
            if (errorMessage.contains("已存在")) {
                return ApiResponse.error(errorMessage);
            }
            return ApiResponse.error("输入内容不合法，请重新输入");
        }
    }

    /**
     * 根据用户名查询用户
     * GET /api/user/username/{username}
     */
    @GetMapping("/username/{username}")
    public ApiResponse<UserEntity> getUserByUsername(@PathVariable String username) {
        try {
            UserEntity user = userService.getUserByUsername(username);
            if (user == null) {
                return ApiResponse.error("用户不存在");
            }
            return ApiResponse.success(user);
        } catch (Exception e) {
            log.error("查询用户失败", e);
            return ApiResponse.serverError("查询失败");
        }
    }

    /**
     * 根据手机号查询用户
     * GET /api/user/phone/{phone}
     */
    @GetMapping("/phone/{phone}")
    public ApiResponse<UserEntity> getUserByPhone(@PathVariable String phone) {
        try {
            UserEntity user = userService.getUserByPhone(phone);
            if (user == null) {
                return ApiResponse.error("用户不存在");
            }
            return ApiResponse.success(user);
        } catch (Exception e) {
            log.error("查询用户失败", e);
            return ApiResponse.serverError("查询失败");
        }
    }
}
