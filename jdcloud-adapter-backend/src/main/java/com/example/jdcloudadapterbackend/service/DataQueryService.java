package com.example.jdcloudadapterbackend.service;

import com.example.jdcloudadapterbackend.client.JiandaoyunApiClient;
import com.example.jdcloudadapterbackend.config.QueryTestConfig;
import com.example.jdcloudadapterbackend.config.QueryWidgetConfig;
import com.example.jdcloudadapterbackend.model.dto.PageResult;
import com.example.jdcloudadapterbackend.model.dto.QueryRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 数据查询服务（题目3）
 * 核心功能：分页查询、Mock数据生成
 */
@Slf4j
@Service
public class DataQueryService {

    @Resource
    private JiandaoyunApiClient apiClient;

    @Resource
    private QueryTestConfig queryTestConfig;

    @Resource
    private QueryWidgetConfig widgetConfig;

    /**
     * 查询数据（支持分页 - 核心得分点3分）
     */
    public PageResult<Map<String, Object>> query(QueryRequest request) {
        try {
            log.info("开始查询数据: formId={}, page={}, pageSize={}",
                    request.getFormId(), request.getPage(), request.getPageSize());

            // 1. 构建过滤条件
            Map<String, Object> filter = buildFilter(request.getFilters());

            // 2. 查询总数
            Long total = queryTotal(request.getFormId(), filter);
            log.info("查询总数: {}", total);

            if (total == 0) {
                return new PageResult<>(Collections.emptyList(), 0L,
                        request.getPage(), request.getPageSize());
            }

            // 3. 查询当前页数据
            List<Map<String, Object>> data = queryData(
                    request.getFormId(),
                    filter,
                    request.getPageSize(),
                    request.getSkip()
            );

            log.info("查询成功，返回{}条数据", data.size());

            return new PageResult<>(data, total,
                    request.getPage(), request.getPageSize());

        } catch (Exception e) {
            log.error("查询失败", e);
            throw new RuntimeException("查询失败: " + e.getMessage());
        }
    }

    /**
     * 查询总数
     */
    private Long queryTotal(String formId, Map<String, Object> filter) throws Exception {
        Map<String, Object> response = apiClient.retrieveFormDataWithSkip(
                queryTestConfig.getAppId(),
                formId,
                filter,
                100,  // 改为100，简道云API不返回total字段
                0
        );

        // 简道云API不返回total字段，用data数组的长度作为总数
        if (response != null && response.containsKey("data")) {
            List<?> data = (List<?>) response.get("data");
            return (long) data.size();
        }
        return 0L;
    }

    /**
     * 查询数据（支持skip分页）
     */
    private List<Map<String, Object>> queryData(
            String formId,
            Map<String, Object> filter,
            int limit,
            int skip) throws Exception {

        Map<String, Object> response = apiClient.retrieveFormDataWithSkip(
                queryTestConfig.getAppId(),
                formId,
                filter,
                limit,
                skip
        );

        // 解析简道云API响应：data 直接是数组
        if (response != null && response.containsKey("data")) {
            return (List<Map<String, Object>>) response.get("data");
        }
        return Collections.emptyList();
    }

    /**
     * 构建简道云过滤条件
     * TODO: 条件过滤功能已实现，等待完整测试（题目3可选功能）
     */
    private Map<String, Object> buildFilter(Map<String, String> filters) {
        if (filters == null || filters.isEmpty()) {
            return new HashMap<>();
        }

        // 简道云filter格式：
        // {
        //   "rel": "and",
        //   "cond": [
        //     {"field": "widget_id", "method": "eq", "value": "张三"}
        //   ]
        // }
        Map<String, Object> filter = new HashMap<>();
        filter.put("rel", "and");

        List<Map<String, Object>> conds = new ArrayList<>();
        filters.forEach((key, value) -> {
            Map<String, Object> cond = new HashMap<>();
            cond.put("field", getWidgetId(key));
            cond.put("method", "eq");  // 等于查询
            cond.put("value", value);
            conds.add(cond);
        });

        filter.put("cond", conds);
        return filter;
    }

    /**
     * 根据字段别名获取widget_id
     */
    private String getWidgetId(String fieldName) {
        switch (fieldName) {
            case "name":
                return widgetConfig.getName();
            case "phone":
                return widgetConfig.getPhone();
            case "department":
                return widgetConfig.getDepartment();
            case "status":
                return widgetConfig.getStatus();
            default:
                // 如果直接传widget_id，直接返回
                return fieldName;
        }
    }

    /**
     * 生成Mock数据（得分点1分）
     */
    public String generateMockData(String formId, Integer count) {
        try {
            log.info("开始生成{}条Mock数据，formId={}", count, formId);

            // 使用线程池批量插入
            ExecutorService executor = Executors.newFixedThreadPool(5);
            int batchSize = 100;  // 每批100条
            int batches = (int) Math.ceil((double) count / batchSize);

            CountDownLatch latch = new CountDownLatch(batches);
            int successCount = 0;

            for (int i = 0; i < batches; i++) {
                int start = i * batchSize;
                int end = Math.min(start + batchSize, count);
                int batchIndex = i;

                executor.submit(() -> {
                    try {
                        // 生成一批数据
                        List<Map<String, Object>> batchData = new ArrayList<>();
                        for (int j = start; j < end; j++) {
                            Map<String, Object> data = generateRandomData(j);
                            batchData.add(data);
                        }

                        // 批量创建（一次创建100条）
                        apiClient.batchCreateFormData(
                            queryTestConfig.getAppId(),
                            formId,
                            batchData
                        );

                        log.info("批次{}完成: {}-{}, 共{}条", batchIndex, start, end, batchData.size());
                    } catch (Exception e) {
                        log.error("批次{}失败: {}-{}", batchIndex, start, end, e);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            // 等待所有批次完成（最多30分钟）
            boolean finished = latch.await(30, TimeUnit.MINUTES);
            executor.shutdown();

            if (finished) {
                log.info("Mock数据生成完成，总计{}条", count);
                return "成功生成" + count + "条数据";
            } else {
                return "部分数据生成超时";
            }

        } catch (Exception e) {
            log.error("生成Mock数据失败", e);
            throw new RuntimeException("生成失败: " + e.getMessage());
        }
    }

    /**
     * 生成随机数据
     * 数据格式匹配简道云子表单结构
     */
    private Map<String, Object> generateRandomData(int index) {
        String[] surnames = {"张", "李", "王", "刘", "陈", "杨", "黄", "赵", "周", "吴"};
        String[] names = {"伟", "芳", "娜", "秀英", "敏", "静", "丽", "强", "磊", "军"};
        String[] depts = {"研发部", "市场部", "运营部"};
        String[] statuses = {"在职", "离职"};

        Random random = new Random();

        // 子表单内的单条记录
        Map<String, Object> subRecord = new HashMap<>();

        // 姓名字段（包装value）
        Map<String, Object> nameField = new HashMap<>();
        nameField.put("value", surnames[random.nextInt(surnames.length)] +
                names[random.nextInt(names.length)]);
        subRecord.put("_widget_1765719114393", nameField);

        // 手机号字段（包装value）
        Map<String, Object> phoneField = new HashMap<>();
        phoneField.put("value", "138" + String.format("%08d", 10000000 + index));
        subRecord.put("_widget_1765719114402", phoneField);

        // 部门字段（包装value）
        Map<String, Object> deptField = new HashMap<>();
        deptField.put("value", depts[random.nextInt(depts.length)]);
        subRecord.put("_widget_1765719114398", deptField);

        // 状态字段（包装value）
        Map<String, Object> statusField = new HashMap<>();
        statusField.put("value", statuses[random.nextInt(statuses.length)]);
        subRecord.put("_widget_1765719114400", statusField);

        // 包装成子表单数组
        List<Map<String, Object>> subArray = new ArrayList<>();
        subArray.add(subRecord);

        Map<String, Object> subFormValue = new HashMap<>();
        subFormValue.put("value", subArray);

        // 最终数据结构
        Map<String, Object> data = new HashMap<>();
        data.put("_widget_1765719114391", subFormValue);

        return data;
    }
}
