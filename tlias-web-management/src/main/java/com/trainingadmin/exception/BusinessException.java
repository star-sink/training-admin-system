package com.trainingadmin.exception;

/**
 * 业务异常
 *
 * <p>用于表示可预期的业务校验失败（例如修改或删除的班级不存在）。
 * 该异常由全局异常处理器 {@link GlobaExceptionHandler} 捕获，
 * 并将异常信息原样返回给前端（code = 0），避免被当成系统错误统一提示。</p>
 */
public class BusinessException extends RuntimeException {

    /**
     * @param message 需要直接提示给用户的业务错误信息
     */
    public BusinessException(String message) {
        super(message);
    }
}
