package com.example.jdcloudadapterbackend.controller;

import com.example.jdcloudadapterbackend.model.request.JiandaoyunDataCreateRequest;
import com.example.jdcloudadapterbackend.model.response.ApiResponse;
import com.example.jdcloudadapterbackend.service.JiandaoyunDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 简道云数据控制器
 * 提供简道云数据操作接口
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/jiandaoyun/data")
public class JiandaoyunDataController {

    private final JiandaoyunDataService jiandaoyunDataService;

    public JiandaoyunDataController(JiandaoyunDataService jiandaoyunDataService) {
        this.jiandaoyunDataService = jiandaoyunDataService;
    }

    /**
     * 创建简道云数据
     * POST /jiandaoyun/data/create
     *
     * @param request 简道云数据请求
     * @return 创建结果
     */
    @PostMapping("/create")
    public ApiResponse<Map<String, Object>> createData(@Valid @RequestBody JiandaoyunDataCreateRequest request) {
        try {
            log.info("收到简道云数据创建请求: app_id={}, entry_id={}, transaction_id={}",
                     request.getApp_id(), request.getEntry_id(), request.getTransaction_id());

            // 调用Service创建数据
            Map<String, Object> result = jiandaoyunDataService.createData(request);

            return ApiResponse.success(result);

        } catch (Exception e) {
            log.error("简道云数据创建失败", e);
            return ApiResponse.error("数据创建失败: " + e.getMessage());
        }
    }
}