package com.net.common.exception;

/**
 * 自定义异常类
 * @author wxy
 */
public class NetRuntimeException extends RuntimeException {
    public NetRuntimeException(String message) {
        super(message);
    }
}
