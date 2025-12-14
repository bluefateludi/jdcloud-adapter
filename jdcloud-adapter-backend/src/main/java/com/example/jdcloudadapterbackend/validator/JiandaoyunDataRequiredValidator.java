package com.example.jdcloudadapterbackend.validator;

import com.example.jdcloudadapterbackend.model.request.JiandaoyunDataCreateRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Map;

/**
 * 简道云数据必填字段校验器
 */
public class JiandaoyunDataRequiredValidator implements ConstraintValidator<JiandaoyunDataRequired, JiandaoyunDataCreateRequest> {

    @Override
    public boolean isValid(JiandaoyunDataCreateRequest request, ConstraintValidatorContext context) {
        Map<String, Object> data = request.getData();

        // 如果data为空，让@NotNull注解处理
        if (data == null || data.isEmpty()) {
            return true;
        }

        // 检查是否包含必要的数据结构
        boolean hasValidData = false;

        // 检查data中的widget数据
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Map) {
                Map<?, ?> widgetMap = (Map<?, ?>) value;
                Object widgetValue = widgetMap.get("value");

                // 如果value是数组且不为空
                if (widgetValue instanceof java.util.List) {
                    java.util.List<?> valueList = (java.util.List<?>) widgetValue;
                    if (!valueList.isEmpty()) {
                        hasValidData = true;
                        break;
                    }
                }
            }
        }

        if (!hasValidData) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("数据内容必须包含至少一个有效的字段值")
                   .addConstraintViolation();
            return false;
        }

        return true;
    }
}