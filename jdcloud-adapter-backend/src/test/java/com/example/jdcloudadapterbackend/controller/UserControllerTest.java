package com.example.jdcloudadapterbackend.controller;

import com.example.jdcloudadapterbackend.model.request.CreateUserRequest;
import com.example.jdcloudadapterbackend.model.entity.UserEntity;
import com.example.jdcloudadapterbackend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * UserController单元测试
 * 使用MockMvc进行Controller层测试
 */
@WebMvcTest(UserController.class)
@DisplayName("用户注册API测试")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserEntity mockUser1;
    private UserEntity mockUser2;

    @BeforeEach
    public void setUp() {
        // 准备测试数据 - 张三
        mockUser1 = new UserEntity();
        mockUser1.setId(1L);
        mockUser1.setUsername("zhangsan001");
        mockUser1.setPhone("13065100224");
        mockUser1.setStatus(1);

        // 准备测试数据 - 赵
        mockUser2 = new UserEntity();
        mockUser2.setId(2L);
        mockUser2.setUsername("zhao002");
        mockUser2.setPhone("15065100224");
        mockUser2.setStatus(1);
    }

    @Test
    @DisplayName("测试1: 正常注册用户 - 张三")
    public void testRegisterSuccess_ZhangSan() throws Exception {
        // 准备请求数据
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("zhangsan001");
        request.setPhone("13065100224");

        // Mock Service行为
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(mockUser1);

        // 执行测试
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.username").value("zhangsan001"))
                .andExpect(jsonPath("$.data.phone").value("13065100224"))
                .andExpect(jsonPath("$.data.status").value(1));
    }

    @Test
    @DisplayName("测试2: 正常注册用户 - 赵")
    public void testRegisterSuccess_Zhao() throws Exception {
        // 准备请求数据
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("zhao002");
        request.setPhone("15065100224");

        // Mock Service行为
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(mockUser2);

        // 执行测试
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("zhao002"))
                .andExpect(jsonPath("$.data.phone").value("15065100224"));
    }

    @Test
    @DisplayName("测试3: 重复注册 - 用户名已存在")
    public void testRegisterFail_DuplicateUsername() throws Exception {
        // 准备请求数据
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("zhangsan001");
        request.setPhone("13912345678");

        // Mock Service抛出异常
        when(userService.createUser(any(CreateUserRequest.class)))
                .thenThrow(new RuntimeException("用户名已存在"));

        // 执行测试
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(containsString("已存在")));
    }

    @Test
    @DisplayName("测试4: 重复注册 - 手机号已存在")
    public void testRegisterFail_DuplicatePhone() throws Exception {
        // 准备请求数据
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("lisi003");
        request.setPhone("13065100224");

        // Mock Service抛出异常
        when(userService.createUser(any(CreateUserRequest.class)))
                .thenThrow(new RuntimeException("手机号已存在"));

        // 执行测试
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(containsString("已存在")));
    }

    @Test
    @DisplayName("测试5: 参数校验 - 用户名为空")
    public void testValidation_EmptyUsername() throws Exception {
        // 准备请求数据
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("");
        request.setPhone("13800138000");

        // 执行测试
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("测试6: 参数校验 - 手机号为空")
    public void testValidation_EmptyPhone() throws Exception {
        // 准备请求数据
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");
        request.setPhone("");

        // 执行测试
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("测试7: 参数校验 - 用户名长度不足")
    public void testValidation_UsernameTooShort() throws Exception {
        // 准备请求数据
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("a");
        request.setPhone("13800138000");

        // 执行测试
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("测试8: 参数校验 - 用户名包含特殊字符")
    public void testValidation_UsernameSpecialChar() throws Exception {
        // 准备请求数据
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test@user");
        request.setPhone("13800138000");

        // 执行测试
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("测试9: 参数校验 - 用户名包含中文")
    public void testValidation_UsernameChinese() throws Exception {
        // 准备请求数据
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("张三");
        request.setPhone("13800138000");

        // 执行测试
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("测试10: 参数校验 - 手机号格式错误(不是11位)")
    public void testValidation_PhoneWrongLength() throws Exception {
        // 准备请求数据
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");
        request.setPhone("138001380");

        // 执行测试
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("测试11: 参数校验 - 手机号首位不是1")
    public void testValidation_PhoneWrongStart() throws Exception {
        // 准备请求数据
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");
        request.setPhone("23800138000");

        // 执行测试
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("测试12: 根据用户名查询 - 成功")
    public void testGetUserByUsername_Success() throws Exception {
        // Mock Service行为
        when(userService.getUserByUsername("zhangsan001")).thenReturn(mockUser1);

        // 执行测试
        mockMvc.perform(get("/api/user/username/zhangsan001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("zhangsan001"))
                .andExpect(jsonPath("$.data.phone").value("13065100224"));
    }

    @Test
    @DisplayName("测试13: 根据用户名查询 - 用户不存在")
    public void testGetUserByUsername_NotFound() throws Exception {
        // Mock Service行为
        when(userService.getUserByUsername("notexist999")).thenReturn(null);

        // 执行测试
        mockMvc.perform(get("/api/user/username/notexist999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("用户不存在"));
    }

    @Test
    @DisplayName("测试14: 根据手机号查询 - 成功")
    public void testGetUserByPhone_Success() throws Exception {
        // Mock Service行为
        when(userService.getUserByPhone("13065100224")).thenReturn(mockUser1);

        // 执行测试
        mockMvc.perform(get("/api/user/phone/13065100224"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("zhangsan001"))
                .andExpect(jsonPath("$.data.phone").value("13065100224"));
    }

    @Test
    @DisplayName("测试15: 根据手机号查询 - 手机号不存在")
    public void testGetUserByPhone_NotFound() throws Exception {
        // Mock Service行为
        when(userService.getUserByPhone("19999999999")).thenReturn(null);

        // 执行测试
        mockMvc.perform(get("/api/user/phone/19999999999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("用户不存在"));
    }
}
