package com.example.jdcloudadapterbackend.model.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * 数据查询请求（题目3）
 */
@Data
public class QueryRequest {

    /** 表单ID */
    @NotBlank(message = "表单ID不能为空")
    private String formId;

    /** 查询条件（可选，如：{"name": "张三", "department": "研发部"}） */
    private Map<String, String> filters;

    /** 当前页（从1开始） */
    @Min(value = 1, message = "页码必须大于0")
    private Integer page = 1;

    /** 每页大小 */
    @Min(value = 1, message = "每页大小必须大于0")
    private Integer pageSize = 20;

    /** 计算skip值（简道云API分页参数） */
    public int getSkip() {
        return (page - 1) * pageSize;
    }
}
