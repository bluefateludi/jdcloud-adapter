package com.example.jdcloudadapterbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.jdcloudadapterbackend.client.JiandaoyunApiClient;
import com.example.jdcloudadapterbackend.config.JiandaoyunApiConfig;
import com.example.jdcloudadapterbackend.mapper.UserMapper;
import com.example.jdcloudadapterbackend.model.request.CreateUserRequest;
import com.example.jdcloudadapterbackend.model.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务
 * 题目1：处理用户注册业务逻辑
 */
@Slf4j
@Service
public class UserService {

    private final UserMapper userMapper;
    private final JiandaoyunApiClient apiClient;
    private final JiandaoyunApiConfig apiConfig;

    public UserService(UserMapper userMapper,
            JiandaoyunApiClient apiClient,
            JiandaoyunApiConfig apiConfig) {
        this.userMapper = userMapper;
        this.apiClient = apiClient;
        this.apiConfig = apiConfig;
    }

    /**
     * 创建用户
     * 1. 校验用户名和手机号唯一性（并发控制）
     * 2. 调用简道云通讯录API创建成员
     * 3. 调用简道云数据API写入表单数据
     * 4. 保存到本地数据库
     *
     * @param request 用户注册请求
     * @return 用户实体
     */
    @Transactional(rollbackFor = Exception.class)
    public UserEntity createUser(CreateUserRequest request) {
        // 1. 校验用户名唯一性（数据库层面已有唯一索引，这里做并发控制）
        QueryWrapper<UserEntity> usernameQuery = new QueryWrapper<>();
        usernameQuery.eq("username", request.getUsername());
        if (userMapper.selectCount(usernameQuery) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 2. 校验手机号唯一性
        QueryWrapper<UserEntity> phoneQuery = new QueryWrapper<>();
        phoneQuery.eq("phone", request.getPhone());
        if (userMapper.selectCount(phoneQuery) > 0) {
            throw new RuntimeException("手机号已存在");
        }

        try {
            // 3. (跳过)调用简道云通讯录API创建成员 - 需要企业权限，暂时注释
            // log.info("开始创建简道云成员: username={}, phone={}", request.getUsername(),
            // request.getPhone());
            // String jdcloudUserId = apiClient.createMember(request.getUsername(),
            // request.getPhone());
            // log.info("简道云成员创建成功: userId={}", jdcloudUserId);

            // 4. 调用简道云数据API写入【用户基础表】(题目核心要求)
            log.info("开始写入简道云用户基础表: username={}, phone={}", request.getUsername(), request.getPhone());
            Map<String, Object> formData = new HashMap<>();
            formData.put("user_name", request.getUsername());
            formData.put("phone", request.getPhone());
            formData.put("status", "启用"); // 字符串格式，与简道云表单设计一致

            String jdcloudDataId = apiClient.createFormData(
                    apiConfig.getFormIdUserBase(),
                    formData);
            log.info("简道云表单数据创建成功: dataId={}", jdcloudDataId);

            // 5. 保存到本地数据库
            UserEntity user = new UserEntity();
            user.setUsername(request.getUsername());
            user.setPhone(request.getPhone());
            user.setStatus(1); // 1=启用

            userMapper.insert(user);
            log.info("用户注册成功: id={}, username={}", user.getId(), user.getUsername());

            return user;

        } catch (Exception e) {
            log.error("创建用户失败", e);
            throw new RuntimeException("创建用户失败：" + e.getMessage());
        }
    }

    /**
     * 根据用户名查询用户
     */
    public UserEntity getUserByUsername(String username) {
        QueryWrapper<UserEntity> query = new QueryWrapper<>();
        query.eq("username", username);
        return userMapper.selectOne(query);
    }

    /**
     * 根据手机号查询用户
     */
    public UserEntity getUserByPhone(String phone) {
        QueryWrapper<UserEntity> query = new QueryWrapper<>();
        query.eq("phone", phone);
        return userMapper.selectOne(query);
    }
}
