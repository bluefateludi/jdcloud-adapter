package com.example.jdcloudadapterbackend.controller;

import com.example.jdcloudadapterbackend.model.dto.PageResult;
import com.example.jdcloudadapterbackend.model.dto.QueryRequest;
import com.example.jdcloudadapterbackend.model.response.ApiResponse;
import com.example.jdcloudadapterbackend.service.DataQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 题目3：数据查询接口
 * 提供分页查询和Mock数据生成功能
 */
@Slf4j
@RestController
@RequestMapping("/data")
public class DataQueryController {

    @Resource
    private DataQueryService dataQueryService;

    /**
     * 查询数据（支持分页）
     * POST /api/data/query
     *
     * 请求示例：
     * {
     *   "formId": "59264073a2a60c0c08e20bfd",
     *   "filters": {
     *     "name": "张三",
     *     "department": "研发部"
     *   },
     *   "page": 1,
     *   "pageSize": 20
     * }
     */
    @PostMapping("/query")
    public ApiResponse<PageResult<Map<String, Object>>> query(
            @Validated @RequestBody QueryRequest request) {

        log.info("接收查询请求: formId={}, filters={}, page={}, pageSize={}",
                request.getFormId(),
                request.getFilters(),
                request.getPage(),
                request.getPageSize());

        PageResult<Map<String, Object>> result = dataQueryService.query(request);

        return ApiResponse.success(result);
    }

    /**
     * 生成Mock数据
     * POST /api/data/mock?formId=xxx&count=50000
     *
     * 参数说明：
     * - formId: 表单ID（必填）
     * - count: 生成数量（默认50000）
     */
    @PostMapping("/mock")
    public ApiResponse<String> generateMock(
            @RequestParam String formId,
            @RequestParam(defaultValue = "50000") Integer count) {

        log.info("接收Mock数据生成请求: formId={}, count={}", formId, count);

        String result = dataQueryService.generateMockData(formId, count);

        return ApiResponse.success(result);
    }
}
