package com.example.jdcloudadapterbackend.model.request;

import com.example.jdcloudadapterbackend.validator.JiandaoyunDataRequired;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.*;
import java.util.Map;

/**
 * 简道云数据创建请求
 * 对应简道云API的JSON格式
 */
@Data
@JiandaoyunDataRequired
public class JiandaoyunDataCreateRequest {

    /**
     * 应用ID
     */
    @NotBlank(message = "应用ID不能为空")
    @Length(min = 20, message = "应用ID长度不能少于20位")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "应用ID只能包含字母和数字")
    private String app_id;

    /**
     * 表单ID
     */
    @NotBlank(message = "表单ID不能为空")
    @Length(min = 20, message = "表单ID长度不能少于20位")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "表单ID只能包含字母和数字")
    private String entry_id;

    /**
     * 事务ID
     */
    @NotBlank(message = "事务ID不能为空")
    @Length(min = 10, max = 50, message = "事务ID长度必须在10-50位之间")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "事务ID只能包含字母、数字、下划线和连字符")
    private String transaction_id;

    /**
     * 数据内容
     */
    @NotNull(message = "数据内容不能为空")
    @Size(min = 1, message = "数据内容不能为空")
    private Map<String, Object> data;
}