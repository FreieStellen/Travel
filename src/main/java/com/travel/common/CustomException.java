package com.travel.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
 *@ClassName CustomException
 *@Author Freie  stellen
 *@Date 2024/4/17 9:05
 */
@RestControllerAdvice
public class CustomException extends RuntimeException {
    @ExceptionHandler(Exception.class)
    public ResponseResult<String> doException(Exception e) {
        return ResponseResult.error(e.getMessage());
    }
}
