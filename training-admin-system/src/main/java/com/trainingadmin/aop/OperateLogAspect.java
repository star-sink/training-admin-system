package com.trainingadmin.aop;

import com.trainingadmin.mapper.OperateLogMapper;
import com.trainingadmin.pojo.OperateLog;
import com.trainingadmin.utils.CurrentHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 操作日志切面
 *
 * <p>拦截 com.trainingadmin.controller 包下标注了 {@link com.trainingadmin.anno.Log} 的方法，
 * 记录：操作人、操作时间、目标类全类名、目标方法名、运行时参数、返回值、执行耗时。</p>
 *
 * <p>说明：日志入库失败只打印异常，不影响原有业务方法的执行和返回。</p>
 */
@Slf4j
@Aspect
@Component
public class OperateLogAspect {

    /** operate_log 表中 method_params / return_value 字段长度为 2000 */
    private static final int MAX_TEXT_LENGTH = 2000;

    private final OperateLogMapper operateLogMapper;
    private final JsonMapper jsonMapper;

    public OperateLogAspect(OperateLogMapper operateLogMapper, JsonMapper jsonMapper) {
        this.operateLogMapper = operateLogMapper;
        this.jsonMapper = jsonMapper;
    }

    @Around("@annotation(com.trainingadmin.anno.Log)")
    public Object recordOperateLog(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = null;
        try {
            // 执行目标方法（增、删、改业务）
            result = joinPoint.proceed();
            return result;
        } finally {
            // 无论成功或异常，都记录本次操作的耗时与信息

            long costTime = System.currentTimeMillis() - start;
            try {
                OperateLog operateLog = new OperateLog();
                operateLog.setOperateEmpId(CurrentHolder.getCurrentId());
                operateLog.setOperateTime(LocalDateTime.now());
                operateLog.setClassName(joinPoint.getTarget().getClass().getName());
                operateLog.setMethodName(joinPoint.getSignature().getName());
                operateLog.setMethodParams(toText(joinPoint.getArgs()));
                operateLog.setReturnValue(toText(result));
                operateLog.setCostTime(costTime);
                operateLogMapper.insert(operateLog);
            } catch (Exception e) {
                // 记录日志失败不能影响业务结果
                log.error("记录操作日志失败", e);
            }
        }
    }

    /**
     * 将参数 / 返回值序列化为字符串，并截断到数据库字段允许的长度。
     * 序列化失败时退化为 toString，保证日志本身不会抛异常。
     */
    private String toText(Object value) {
        if (value == null) {
            return null;
        }
        String text;
        try {
            text = jsonMapper.writeValueAsString(value);
        } catch (Exception e) {
            text = (value instanceof Object[])
                    ? Arrays.deepToString((Object[]) value)
                    : String.valueOf(value);
        }
        if (text != null && text.length() > MAX_TEXT_LENGTH) {
            text = text.substring(0, MAX_TEXT_LENGTH);
        }
        return text;
    }
}
