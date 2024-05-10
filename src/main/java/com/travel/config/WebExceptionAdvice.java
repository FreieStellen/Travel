package com.travel.config;

import com.travel.common.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
 *@ClassName WebExceptionAdvice 自定义异常类
 *@Author Freie  stellen
 *@Date 2024/5/8 17:48
 */
@Slf4j
@RestControllerAdvice
public class WebExceptionAdvice {


    //所有的异常在这里统一返回
    @ExceptionHandler(Exception.class)
    public ResponseResult<String> handleException(Exception e) {
        log.error(e.toString(), e);
        return ResponseResult.error(e.getMessage());
    }
}
