package com.example.jdcloudadapterbackend.service;

import com.example.jdcloudadapterbackend.client.JiandaoyunApiClient;
import com.example.jdcloudadapterbackend.config.JiandaoyunApiConfig;
import com.example.jdcloudadapterbackend.model.request.JiandaoyunDataCreateRequest;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 简道云数据服务
 * 处理简道云数据的增删改查操作
 */
@Slf4j
@Service
public class JiandaoyunDataService {

    private final JiandaoyunApiClient jiandaoyunApiClient;
    private final JiandaoyunApiConfig config;
    private final Gson gson;
    private OkHttpClient httpClient;

    public JiandaoyunDataService(JiandaoyunApiClient jiandaoyunApiClient, JiandaoyunApiConfig config) {
        this.jiandaoyunApiClient = jiandaoyunApiClient;
        this.config = config;
        this.gson = new Gson();

        // 初始化OkHttpClient
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(config.getTimeout(), java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(config.getTimeout(), java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(config.getTimeout(), java.util.concurrent.TimeUnit.SECONDS)
                .build();
    }

    /**
     * 创建简道云数据
     * @param request 简道云数据请求
     * @return 创建结果
     */
    public Map<String, Object> createData(JiandaoyunDataCreateRequest request) throws Exception {
        log.info("开始创建简道云数据: app_id={}, entry_id={}", request.getApp_id(), request.getEntry_id());

        // 验证必要参数
        if (request.getApp_id() == null || request.getEntry_id() == null || request.getData() == null) {
            throw new IllegalArgumentException("app_id、entry_id和data不能为空");
        }

        // 直接调用简道云API，使用原始格式
        Map<String, Object> result = createDataDirectly(request);

        log.info("简道云数据创建成功");
        return result;
    }

    /**
     * 直接调用简道云API创建数据
     * @param request 请求参数
     * @return 创建结果
     */
    private Map<String, Object> createDataDirectly(JiandaoyunDataCreateRequest request) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("app_id", request.getApp_id());
        requestBody.put("entry_id", request.getEntry_id());
        requestBody.put("transaction_id", request.getTransaction_id());
        requestBody.put("data", request.getData());

        String jsonBody = gson.toJson(requestBody);
        log.debug("请求简道云创建数据: {}", jsonBody);

        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.parse("application/json; charset=utf-8")
        );

        String url = config.getBaseUrl() + config.getDataCreate();
        Request httpRequest = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + config.getAppKey())
                .build();

        try (Response response = httpClient.newCall(httpRequest).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            log.debug("简道云API响应: {}", responseBody);

            if (!response.isSuccessful()) {
                throw new RuntimeException("API请求失败: " + response.code() + " - " + responseBody);
            }

            Map<String, Object> responseMap = gson.fromJson(responseBody, Map.class);

            // 返回结果
            Map<String, Object> result = new HashMap<>();
            if (responseMap.containsKey("data_id")) {
                result.put("data_id", responseMap.get("data_id"));
            }
            result.put("app_id", request.getApp_id());
            result.put("entry_id", request.getEntry_id());
            result.put("transaction_id", request.getTransaction_id());

            return result;
        }
    }
}