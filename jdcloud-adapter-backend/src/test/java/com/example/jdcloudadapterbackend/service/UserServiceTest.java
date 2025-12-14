package com.example.jdcloudadapterbackend.service;

import com.example.jdcloudadapterbackend.client.JiandaoyunApiClient;
import com.example.jdcloudadapterbackend.config.JiandaoyunApiConfig;
import com.example.jdcloudadapterbackend.config.JiandaoyunWidgetConfig;
import com.example.jdcloudadapterbackend.constants.AppConstants;
import com.example.jdcloudadapterbackend.mapper.UserMapper;
import com.example.jdcloudadapterbackend.model.request.JiandaoyunDataRequest;
import com.example.jdcloudadapterbackend.model.request.UserInfoItem;
import com.example.jdcloudadapterbackend.model.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserService 测试类
 * 测试新的JSON格式用户注册功能
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private JiandaoyunApiClient apiClient;

    @Mock
    private JiandaoyunApiConfig apiConfig;

    @Mock
    private JiandaoyunWidgetConfig widgetConfig;

    @Mock
    private AppConstants appConstants;

    @InjectMocks
    private UserService userService;

    private JiandaoyunDataRequest testRequest;
    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        // 设置测试用的简道云数据请求
        testRequest = new JiandaoyunDataRequest();
            testRequest.setApp_id("693b9c8ea514c5f626f9e472");
        testRequest.setEntry_id("693b9c98bae0580052c38380");
        testRequest.setTransaction_id("test-004-1765081234567");

        // 构建用户信息
        JiandaoyunDataRequest.DataContent dataContent = new JiandaoyunDataRequest.DataContent();
        JiandaoyunDataRequest.UserInfoValue userInfoValue = new JiandaoyunDataRequest.UserInfoValue();

        // 创建用户信息项
        UserInfoItem userInfo = new UserInfoItem();

        // 设置用户ID
        UserInfoItem.UserIdValue userIdValue = new UserInfoItem.UserIdValue();
        userIdValue.setValue(1002L);
        userInfo.setUser_id(userIdValue);

        // 设置用户名
        UserInfoItem.StringValue userNameValue = new UserInfoItem.StringValue();
        userNameValue.setValue("test005");
        userInfo.setUser_name(userNameValue);

        // 设置手机号
        UserInfoItem.StringValue phoneValue = new UserInfoItem.StringValue();
        phoneValue.setValue("13600135000");
        userInfo.setPhone(phoneValue);

        // 设置状态
        UserInfoItem.StringValue statusValue = new UserInfoItem.StringValue();
        statusValue.setValue("启用");
        userInfo.setStatus(statusValue);

        userInfoValue.setValue(Collections.singletonList(userInfo));
        dataContent.setUser_info(userInfoValue);
        testRequest.setData(dataContent);

        // 设置测试用户实体
        testUser = new UserEntity();
        testUser.setId(1L);
        testUser.setUsername("test005");
        testUser.setPhone("13600135000");
        testUser.setStatus(1);

        // 设置API配置
        when(apiConfig.getFormIdUserBase()).thenReturn("test_form_id");

        // 设置Widget配置
        when(widgetConfig.getUser_id()).thenReturn("_widget_1765587253510");
        when(widgetConfig.getUser_name()).thenReturn("_widget_1765514393522");
        when(widgetConfig.getPhone()).thenReturn("_widget_1765586777737");
        when(widgetConfig.getStatus()).thenReturn("_widget_1765602268689");

        // 设置App常量配置
        AppConstants.Error error = new AppConstants.Error();
        error.setUsernameExists("用户名已存在");
        error.setPhoneExists("手机号已存在");
        when(appConstants.getError()).thenReturn(error);

        AppConstants.Transaction transaction = new AppConstants.Transaction();
        transaction.setPrefix("test-");
        when(appConstants.getTransaction()).thenReturn(transaction);

        AppConstants.User user = new AppConstants.User();
        AppConstants.User.Status status = new AppConstants.User.Status();
        status.setEnabled("启用");
        status.setDisabled("禁用");
        user.setStatus(status);
        when(appConstants.getUser()).thenReturn(user);
    }

    @Test
    void testCreateUserDataWithNewFormat_Success() throws Exception {
        // 模拟数据库查询 - 用户名不存在
        when(userMapper.selectCount(any())).thenReturn(0L);
        // 模拟数据库插入成功
        when(userMapper.insert(any(UserEntity.class))).thenReturn(1);
        // 模拟简道云API调用成功
        when(apiClient.createFormData(anyString(), any())).thenReturn("test_data_id");

        // 执行测试
        boolean result = userService.createUserDataWithNewFormat(testRequest);

        // 验证结果
        assertTrue(result);

        // 验证是否调用了数据库查询
        verify(userMapper, atLeast(2)).selectCount(any());

        // 验证是否调用了数据库插入
        verify(userMapper, times(1)).insert(any(UserEntity.class));

        // 验证是否调用了简道云API
        verify(apiClient, times(1)).createFormData(eq("test_form_id"), any());
    }

    @Test
    void testCreateUserDataWithNewFormat_UsernameExists() throws Exception {
        // 模拟数据库查询 - 用户名已存在
        when(userMapper.selectCount(any())).thenReturn(1L);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUserDataWithNewFormat(testRequest);
        });

        assertEquals("用户名已存在: test005", exception.getMessage());

        // 验证没有执行数据库插入
        verify(userMapper, never()).insert(any());

        // 验证没有调用简道云API
        verify(apiClient, never()).createFormData(any(), any());
    }

    @Test
    void testCreateUserDataWithNewFormat_PhoneExists() throws Exception {
        // 模拟数据库查询 - 用户名不存在，但手机号存在
        when(userMapper.selectCount(any()))
            .thenReturn(0L) // 第一次查询用户名
            .thenReturn(1L); // 第二次查询手机号

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUserDataWithNewFormat(testRequest);
        });

        assertEquals("手机号已存在: 13600135000", exception.getMessage());

        // 验证没有执行数据库插入
        verify(userMapper, never()).insert(any());

        // 验证没有调用简道云API
        verify(apiClient, never()).createFormData(any(), any());
    }

    @Test
    void testCreateUserDataWithNewFormat_EmptyData() throws Exception {
        // 创建空数据的请求
        JiandaoyunDataRequest emptyRequest = new JiandaoyunDataRequest();
        emptyRequest.setData(new JiandaoyunDataRequest.DataContent());

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUserDataWithNewFormat(emptyRequest);
        });

        assertEquals("用户信息数据不能为空", exception.getMessage());
    }

    @Test
    void testBuildJiandaoyunDataRequest() {
        // 执行测试
        JiandaoyunDataRequest result = userService.buildJiandaoyunDataRequest(
            "testUser", "13800138000", "启用", 1003L);

        // 验证结果
        assertNotNull(result);
        assertEquals("693b9c8ea514c5f626f9e472", result.getApp_id());
        assertEquals("693b9c98bae0580052c38380", result.getEntry_id());
        assertNotNull(result.getTransaction_id());
        assertTrue(result.getTransaction_id().startsWith("test-004-"));

        // 验证用户信息
        assertNotNull(result.getData());
        assertNotNull(result.getData().getUser_info());
        assertNotNull(result.getData().getUser_info().getValue());
        assertEquals(1, result.getData().getUser_info().getValue().size());

        UserInfoItem userInfo = result.getData().getUser_info().getValue().get(0);
        assertEquals(1003L, userInfo.getUser_id().getValue());
        assertEquals("testUser", userInfo.getUser_name().getValue());
        assertEquals("13800138000", userInfo.getPhone().getValue());
        assertEquals("启用", userInfo.getStatus().getValue());
    }

    @Test
    void testBuildJiandaoyunDataRequest_DisabledUser() {
        // 执行测试 - 禁用状态的用户
        JiandaoyunDataRequest result = userService.buildJiandaoyunDataRequest(
            "disabledUser", "13800138001", "禁用", 1004L);

        // 验证状态
        assertEquals("禁用", result.getData().getUser_info().getValue().get(0).getStatus().getValue());
    }

    @Test
    void testCreateUserDataWithNewFormat_ApiException() throws Exception {
        // 模拟数据库查询成功
        when(userMapper.selectCount(any())).thenReturn(0L);
        // 模拟数据库插入成功
        when(userMapper.insert(any(UserEntity.class))).thenReturn(1);
        // 模拟简道云API调用失败
        when(apiClient.createFormData(anyString(), any()))
            .thenThrow(new RuntimeException("API调用失败"));

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUserDataWithNewFormat(testRequest);
        });

        assertTrue(exception.getMessage().contains("创建用户数据失败"));
        assertTrue(exception.getMessage().contains("API调用失败"));
    }

    @Test
    void testCreateUserDataWithNewFormat_DatabaseException() throws Exception {
        // 模拟数据库查询成功
        when(userMapper.selectCount(any())).thenReturn(0L);
        // 模拟数据库插入失败
        when(userMapper.insert(any(UserEntity.class)))
            .thenThrow(new RuntimeException("数据库插入失败"));

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUserDataWithNewFormat(testRequest);
        });

        assertTrue(exception.getMessage().contains("创建用户数据失败"));
        assertTrue(exception.getMessage().contains("数据库插入失败"));
    }
}