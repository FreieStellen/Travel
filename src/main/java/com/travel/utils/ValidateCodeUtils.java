package com.travel.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/*
 *@ClassName ValidateCodeUtils 随机验证码生成工具
 *@Author Freie  stellen
 *@Date 2024/3/28 14:36
 */
@Slf4j
public class ValidateCodeUtils {

    /**
     * @Description: 随机生成六位验证码
     * @param:
     * @date: 2024/3/28 14:42
     */

    public static String generateValidateCode() {


        Integer code = new Random().nextInt(999999);

        if (code < 10000) {
            code = code + 100000;
        }

        return code.toString();
    }

    public static Boolean matches(String code, String redisCode) {

        return code.equals(redisCode);
    }
}
