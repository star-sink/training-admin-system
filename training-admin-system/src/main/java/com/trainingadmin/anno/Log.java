package com.trainingadmin.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解
 *
 * <p>标注在 Controller 的增、删、改方法上，由 {@code OperateLogAspect} 拦截，
 * 自动记录操作人、操作时间、目标类、目标方法、参数、返回值、耗时。</p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
}
