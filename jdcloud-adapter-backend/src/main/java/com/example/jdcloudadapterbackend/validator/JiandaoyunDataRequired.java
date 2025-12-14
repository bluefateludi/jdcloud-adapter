package com.example.jdcloudadapterbackend.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 简道云数据必填字段校验
 */
@Documented
@Constraint(validatedBy = JiandaoyunDataRequiredValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JiandaoyunDataRequired {
    String message() default "简道云数据缺少必填字段（主键id、phone、用户名）";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}