package com.example.jdcloudadapterbackend.client;

import com.example.jdcloudadapterbackend.config.JiandaoyunApiConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 简道云API客户端
 * 使用Bearer AppKey认证方式调用简道云开放平台API
 */
@Slf4j
@Component
public class JiandaoyunApiClient {

    private final JiandaoyunApiConfig config;
    private final Gson gson;
    private OkHttpClient httpClient;

    public JiandaoyunApiClient(JiandaoyunApiConfig config) {
        this.config = config;
        this.gson = new Gson();
    }

    @PostConstruct
    public void init() {
        // 初始化OkHttpClient
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(config.getTimeout(), TimeUnit.SECONDS)
                .readTimeout(config.getTimeout(), TimeUnit.SECONDS)
                .writeTimeout(config.getTimeout(), TimeUnit.SECONDS)
                .build();
        log.info("简道云API客户端初始化完成，AppKey: {}", config.getAppKey());
    }

    /**
     * 创建简道云企业成员（通讯录API）
     * @param username 用户名
     * @param mobile 手机号
     * @return 成员ID
     */
    public String createMember(String username, String mobile) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("mobile", mobile);

        String url = config.getBaseUrl() + config.getMemberCreate();
        Map<String, Object> response = post(url, requestBody);

        if (response != null && response.containsKey("user_id")) {
            return response.get("user_id").toString();
        }
        throw new RuntimeException("创建简道云成员失败：" + gson.toJson(response));
    }

    /**
     * 删除简道云企业成员（通讯录API）
     * @param userId 成员ID
     */
    public void deleteMember(String userId) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("user_id", userId);

        String url = config.getBaseUrl() + config.getMemberDelete();
        post(url, requestBody);
        log.info("删除简道云成员成功: {}", userId);
    }

    /**
     * 创建表单数据（数据API）
     * @param formId 表单ID
     * @param data 表单数据（原始值，不需要包装成{"value": ...}格式）
     * @return 数据ID
     */
    public String createFormData(String formId, Map<String, Object> data) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("app_id", config.getAppId());  // 使用AppId
        requestBody.put("entry_id", formId);
        requestBody.put("transaction_id", java.util.UUID.randomUUID().toString());

        // v5 API 需要将每个字段包装为 {"value": "字段值"} 格式
        Map<String, Object> formattedData = new HashMap<>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            Map<String, Object> valueWrapper = new HashMap<>();
            valueWrapper.put("value", entry.getValue());
            formattedData.put(entry.getKey(), valueWrapper);
        }
        requestBody.put("data", formattedData);

        log.debug("请求简道云创建数据: app_id={}, entry_id={}, transaction_id={}, data={}",
                 config.getAppId(), formId, requestBody.get("transaction_id"), gson.toJson(formattedData));

        String url = config.getBaseUrl() + config.getDataCreate();
        Map<String, Object> response = post(url, requestBody);

        if (response != null && response.containsKey("data")) {
            Map<String, Object> responseData = (Map<String, Object>) response.get("data");
            if (responseData.containsKey("_id")) {
                String dataId = responseData.get("_id").toString();
                log.info("成功创建表单数据，data_id: {}", dataId);
                return dataId;
            }
        }
        throw new RuntimeException("创建表单数据失败：" + gson.toJson(response));
    }

    /**
     * 查询表单数据（数据API）
     * @param formId 表单ID
     * @param filter 过滤条件
     * @param limit 查询数量
     * @return 数据列表
     */
    public Map<String, Object> retrieveFormData(String formId, Map<String, Object> filter, int limit) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("app_id", config.getAppId());  // 使用AppId而不是AppKey
        requestBody.put("entry_id", formId);
        requestBody.put("filter", filter);
        requestBody.put("limit", limit);

        String url = config.getBaseUrl() + config.getDataList();
        return post(url, requestBody);
    }

    /**
     * POST请求封装
     * 使用Bearer AppKey认证方式
     */
    private Map<String, Object> post(String url, Map<String, Object> requestBody) throws Exception {
        String jsonBody = gson.toJson(requestBody);

        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + config.getAppKey())
                .build();

        log.debug("简道云API请求: {} - {}", url, jsonBody);

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            log.debug("简道云API响应: {}", responseBody);

            if (!response.isSuccessful()) {
                throw new RuntimeException("API请求失败: " + response.code() + " - " + responseBody);
            }

            return gson.fromJson(responseBody, new TypeToken<Map<String, Object>>() {}.getType());
        }
    }
}
