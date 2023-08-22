package com.yzy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yzy
 * @version 1.0
 * @description ServiceScan注解
 * @date 2023/8/22 13:57
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceScan {
    String value() default "";
}
