package com.example.jdcloudadapterbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.jdcloudadapterbackend.client.JiandaoyunApiClient;
import com.example.jdcloudadapterbackend.config.JiandaoyunApiConfig;
import com.example.jdcloudadapterbackend.config.JiandaoyunWidgetConfig;
import com.example.jdcloudadapterbackend.constants.AppConstants;
import com.example.jdcloudadapterbackend.mapper.UserMapper;
import com.example.jdcloudadapterbackend.model.request.CreateUserRequest;
import com.example.jdcloudadapterbackend.model.request.JiandaoyunDataRequest;
import com.example.jdcloudadapterbackend.model.request.UserInfoItem;
import com.example.jdcloudadapterbackend.model.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Collections;

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
    private final JiandaoyunWidgetConfig widgetConfig;
    private final AppConstants appConstants;

    public UserService(UserMapper userMapper,
            JiandaoyunApiClient apiClient,
            JiandaoyunApiConfig apiConfig,
            JiandaoyunWidgetConfig widgetConfig,
            AppConstants appConstants) {
        this.userMapper = userMapper;
        this.apiClient = apiClient;
        this.apiConfig = apiConfig;
        this.widgetConfig = widgetConfig;
        this.appConstants = appConstants;
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
        validateUsernameUnique(request.getUsername());

        // 2. 校验手机号唯一性
        validatePhoneUnique(request.getPhone());

        try {
            // 3. 调用简道云通讯录API创建成员 todo 先注释掉
            // log.info("开始创建简道云成员: username={}, phone={}", request.getUsername(), request.getPhone());
            // String jdcloudUserId = apiClient.createMember(request.getUsername(), request.getPhone());
            // log.info("简道云成员创建成功: userId={}", jdcloudUserId);

            // 4. 调用简道云数据API写入【用户基础表】(题目核心要求)
            log.info("开始写入简道云用户基础表: username={}, phone={}", request.getUsername(), request.getPhone());
            Map<String, Object> formData = new HashMap<>();

            // 构建简道云需要的数据结构（JiandaoyunApiClient会自动包装为 {"value": ...} 格式）
            Map<String, Object> phoneField = new HashMap<>();
            phoneField.put("phone", request.getPhone());

            // 使用配置中的widget_id
            formData.put(widgetConfig.getUser_id(), request.getUsername()); // ID字段（必填）
            formData.put(widgetConfig.getUser_name(), request.getUsername()); // 用户名字段
            formData.put(widgetConfig.getPhone(), phoneField); // 手机号字段
            formData.put(widgetConfig.getStatus(), appConstants.getUser().getStatus().getEnabled()); // 状态字段

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
     * 使用新的JSON格式创建用户数据
     * 根据提供的JSON格式调用简道云数据API
     *
     * @param request 简道云数据请求
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean createUserDataWithNewFormat(JiandaoyunDataRequest request) {
        try {
            log.info("开始使用新格式写入简道云用户数据: transactionId={}", request.getTransaction_id());

            // 验证请求数据
            if (request.getData() == null || request.getData().getUser_info() == null
                    || request.getData().getUser_info().getValue() == null
                    || request.getData().getUser_info().getValue().isEmpty()) {
                throw new RuntimeException("用户信息数据不能为空");
            }

            // 获取用户信息列表
            List<UserInfoItem> userInfoList = request.getData().getUser_info().getValue();

            // 处理每个用户信息
            for (UserInfoItem userInfo : userInfoList) {
                // 校验用户名唯一性
                if (userInfo.getUser_name() != null && userInfo.getUser_name().getValue() != null) {
                    String username = userInfo.getUser_name().getValue();
                    validateUsernameUnique(username);

                    // 校验手机号唯一性
                    if (userInfo.getPhone() != null && userInfo.getPhone().getValue() != null) {
                        String phone = userInfo.getPhone().getValue();
                        validatePhoneUnique(phone);

                        // 保存到本地数据库
                        UserEntity user = new UserEntity();
                        user.setUsername(username);
                        user.setPhone(phone);
                        user.setStatus(
                                appConstants.getUser().getStatus().getEnabled().equals(userInfo.getStatus().getValue())
                                        ? 1
                                        : 0);

                        userMapper.insert(user);
                        log.info("用户注册成功: id={}, username={}", user.getId(), user.getUsername());
                    }
                }
            }

            // 调用简道云数据API写入表单数据
            // 构建符合简道云API格式的数据
            Map<String, Object> formData = new HashMap<>();

            // 将子表单数据转换为简道云格式
            for (UserInfoItem userInfo : userInfoList) {
                // 使用配置中的widget_id
                if (userInfo.getUser_name() != null) {
                    formData.put(widgetConfig.getUser_name(), userInfo.getUser_name().getValue());
                }
                if (userInfo.getPhone() != null) {
                    Map<String, Object> phoneField = new HashMap<>();
                    phoneField.put("phone", userInfo.getPhone().getValue());
                    formData.put(widgetConfig.getPhone(), phoneField);
                }
                if (userInfo.getStatus() != null) {
                    formData.put(widgetConfig.getStatus(), userInfo.getStatus().getValue());
                }
            }

            String jdcloudDataId = apiClient.createFormData(
                    apiConfig.getFormIdUserBase(),
                    formData);
            log.info("简道云表单数据创建成功: dataId={}", jdcloudDataId);

            return true;

        } catch (Exception e) {
            log.error("使用新格式创建用户数据失败", e);
            throw new RuntimeException("创建用户数据失败：" + e.getMessage());
        }
    }

    /**
     * 创建简道云数据请求对象
     *
     * @param username 用户名
     * @param phone    手机号
     * @param status   状态
     * @param userId   用户ID
     * @return 简道云数据请求
     */
    public JiandaoyunDataRequest buildJiandaoyunDataRequest(String username, String phone, String status, Long userId) {
        JiandaoyunDataRequest request = new JiandaoyunDataRequest();
        request.setApp_id(apiConfig.getAppId()); // 使用配置中的app_id
        request.setEntry_id(apiConfig.getFormIdUserBase()); // 使用配置中的form_id作为entry_id
        request.setTransaction_id(appConstants.getTransaction().getPrefix() + System.currentTimeMillis());

        // 构建用户信息
        UserInfoItem userInfoItem = new UserInfoItem();

        // 设置用户ID
        UserInfoItem.UserIdValue userIdValue = new UserInfoItem.UserIdValue();
        userIdValue.setValue(userId);
        userInfoItem.setUser_id(userIdValue);

        // 设置用户名
        UserInfoItem.StringValue userNameValue = new UserInfoItem.StringValue();
        userNameValue.setValue(username);
        userInfoItem.setUser_name(userNameValue);

        // 设置手机号
        UserInfoItem.StringValue phoneValue = new UserInfoItem.StringValue();
        phoneValue.setValue(phone);
        userInfoItem.setPhone(phoneValue);

        // 设置状态
        UserInfoItem.StringValue statusValue = new UserInfoItem.StringValue();
        statusValue.setValue(status);
        userInfoItem.setStatus(statusValue);

        // 构建数据内容
        JiandaoyunDataRequest.DataContent dataContent = new JiandaoyunDataRequest.DataContent();
        JiandaoyunDataRequest.UserInfoValue userInfoValue = new JiandaoyunDataRequest.UserInfoValue();
        userInfoValue.setValue(Collections.singletonList(userInfoItem));
        dataContent.setUser_info(userInfoValue);

        request.setData(dataContent);

        return request;
    }

    /**
     * 根据手机号查询用户
     */
    public UserEntity getUserByPhone(String phone) {
        QueryWrapper<UserEntity> query = new QueryWrapper<>();
        query.eq("phone", phone);
        return userMapper.selectOne(query);
    }

    /**
     * 校验用户名唯一性
     *
     * @param username 用户名
     */
    private void validateUsernameUnique(String username) {
        QueryWrapper<UserEntity> query = new QueryWrapper<>();
        query.eq("username", username);
        if (userMapper.selectCount(query) > 0) {
            throw new RuntimeException(appConstants.getError().getUsernameExists() + ": " + username);
        }
    }

    /**
     * 校验手机号唯一性
     *
     * @param phone 手机号
     */
    private void validatePhoneUnique(String phone) {
        QueryWrapper<UserEntity> query = new QueryWrapper<>();
        query.eq("phone", phone);
        if (userMapper.selectCount(query) > 0) {
            throw new RuntimeException(appConstants.getError().getPhoneExists() + ": " + phone);
        }
    }
}
