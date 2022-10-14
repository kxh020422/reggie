package com.reggie.common;

/**
 * @ClassName CustomException
 * @Date 2022/10/7 16:42
 * 自定义业务异常
 */
public class CustomException extends RuntimeException {

    public CustomException(String message) {
        super(message);
    }
}
