package com.trainingadmin.interceptor;

import com.trainingadmin.utils.CurrentHolder;
import com.trainingadmin.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 令牌校验拦截器：校验请求头中的 token，未登录或令牌非法返回 401
 */
@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1. 获取请求路径
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}", requestURI);

        //2. 登录请求直接放行（WebConfig 中也排除了 /login，这里双保险）
        if (requestURI.contains("/login")) {
            log.info("登录请求，放行");
            return true;
        }

        //3. 获取请求头中的 token
        String token = request.getHeader("token");

        //4. token 不存在 -> 401，不放行
        if (token == null || token.isEmpty()) {
            log.info("令牌为空，响应401");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        //5. token 校验失败 -> 401，不放行
        try {
            Claims claims = JwtUtils.parseToken(token);
            // 校验通过：把当前登录员工 id 存入 ThreadLocal，供 AOP 记录操作日志使用
            Object id = claims.get("id");
            if (id != null) {
                CurrentHolder.setCurrentId(Integer.valueOf(id.toString()));
            }
        } catch (Exception e) {
            log.info("令牌非法，响应401");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        //6. 校验通过，放行
        log.info("令牌合法，放行");
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求结束清理 ThreadLocal，防止 Tomcat 线程复用导致操作人串号
        CurrentHolder.remove();
    }
}
