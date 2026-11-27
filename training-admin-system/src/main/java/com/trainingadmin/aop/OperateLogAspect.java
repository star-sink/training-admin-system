// 此文件已回滚至 Tlias 原版（按你的要求：AOP 直接解析 token）
package com.trainingadmin.aop;

import com.trainingadmin.mapper.OperateLogMapper;
import com.trainingadmin.pojo.OperateLog;
import com.trainingadmin.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class OperateLogAspect {

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
            result = joinPoint.proceed();
            return result;
        } finally {
            long costTime = System.currentTimeMillis() - start;
            try {
                OperateLog operateLog = new OperateLog();
                operateLog.setOperateEmpId(getCurrentEmpId());
                operateLog.setOperateTime(LocalDateTime.now());
                operateLog.setClassName(joinPoint.getTarget().getClass().getName());
                operateLog.setMethodName(joinPoint.getSignature().getName());
                operateLog.setMethodParams(toText(joinPoint.getArgs()));
                operateLog.setReturnValue(toText(result));
                operateLog.setCostTime(costTime);
                operateLogMapper.insert(operateLog);
            } catch (Exception e) {
                log.error("记录操作日志失败", e);
            }
        }
    }

    private Integer getCurrentEmpId() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            return null;
        }
        Claims claims = JwtUtils.parseJWT(token);
        Object id = claims.get("id");
        return id == null ? null : Integer.valueOf(id.toString());
    }

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
