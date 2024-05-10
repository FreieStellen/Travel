package com.travel.utils;

import lombok.Data;

/*
 *@ClassName RedisConstants 存入redis的一些定义的常量
 *@Author Freie  stellen
 *@Date 2024/3/27 21:03
 */
@Data
public class RedisConstants {

    public static final String LOGIN_CODE_KEY = "login:userId:";
    public static final Long LOGIN_CODE_TTL_MINUTES = 30L;

    public static final Long PHONE_CODE_TTL_MINUTES = 1L;
}
