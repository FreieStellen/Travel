package com.travel.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/*
 *@ClassName ResponseResult 返回给前端的json公共类
 *@Author Freie  stellen
 *@Date 2024/3/23 16:41
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)//返回的数据为null时简化交互数据
public class ResponseResult<T> {

    /**
     * 状态码：成功为1，失败为0
     */
    private Integer code;

    /**
     * 数据
     */
    private T data;

    /**
     * 消息
     */
    private String msg;

    /**
     * @Description: 成功时调用的方法
     * @param: object
     * @date: 2024/3/23 17:02
     */

    public static <T> ResponseResult<T> success(T object) {

        ResponseResult<T> responseResult = new ResponseResult<>();
        responseResult.code = 1;
        responseResult.data = object;
        return responseResult;
    }

    /**
     * @Description: 成功时调用的方法
     * @param: object
     * @date: 2024/3/23 17:02
     */

    public static <T> ResponseResult<T> success(T object, String msg) {

        ResponseResult<T> responseResult = new ResponseResult<>();
        responseResult.code = 1;
        responseResult.data = object;
        responseResult.msg = msg;
        return responseResult;
    }

    /**
     * @Description: 失败时调用的方法
     * @param: msg
     * @date: 2024/3/23 16:59
     */
    public static <T> ResponseResult<T> error(String msg) {

        ResponseResult<T> responseResult = new ResponseResult<>();
        responseResult.code = 0;
        responseResult.msg = msg;
        return responseResult;
    }

}
