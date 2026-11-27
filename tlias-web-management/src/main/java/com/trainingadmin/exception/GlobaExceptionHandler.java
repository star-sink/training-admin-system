package com.trainingadmin.exception;

import com.trainingadmin.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobaExceptionHandler {

    /**
     * 处理业务异常
     *
     * <p>业务异常属于可预期错误（如记录不存在），直接将异常信息返回给前端，
     * 不按系统异常处理，避免提示“出错啦，请联系管理员”。</p>
     *
     * @param e 业务异常
     * @return 统一响应结果，code 为 0，msg 为具体业务提示
     */
    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e) {
        log.warn("业务异常：{}", e.getMessage());
        return Result.error(e.getMessage());
    }

    /**
     * 处理未预见的系统异常
     *
     * <p>记录完整堆栈日志，同时对外返回统一提示，避免暴露内部实现细节。</p>
     *
     * @param e 系统异常
     * @return 统一响应结果，code 为 0
     */
    @ExceptionHandler(value = Exception.class)
    public Result handleException(Exception e) {
        log.error("程序出错啦~",e);
        return Result.error("出错啦，请联系管理员");
    }

    /**
     * 处理唯一键冲突异常
     *
     * <p>例如班级名称重复时，解析数据库异常信息中的重复值并提示给前端。</p>
     *
     * @param e 唯一键冲突异常
     * @return 统一响应结果，code 为 0
     */
    @ExceptionHandler
    public Result handleDuplicateKeyException(DuplicateKeyException e) {
        log.info("程序出错了",e);
        String message = e.getMessage();
        int i=message.indexOf("Duplicate entry");
        String errMsg=message.substring(i);
        String[] arr=errMsg.split(" ");
        return Result.error(arr[2]+"已存在");
    }
}
