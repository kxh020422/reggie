package com.reggie.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @ClassName GlobalExceptionHandler
 * @Date 2022/10/6 15:38
 * 全局异常处理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
public class GlobalExceptionHandler {

    public static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * @date: 2022/10/6 15:42
     * @remark: 进行异常处理方法
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        logger.error(ex.getMessage());
        if (ex.getMessage().contains("Duplicate entry")) {
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }


    /**
     * @date: 2022/10/6 15:42
     * @remark: 进行异常处理方法
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex) {
        logger.error(ex.getMessage());
        return R.error(ex.getMessage());
    }



}
