package com.travel.utils;

import lombok.Data;

import java.util.Random;

/*
 *@ClassName RedisConstants 存入redis的一些定义的常量
 *@Author Freie  stellen
 *@Date 2024/3/27 21:03
 */
@Data
public class RedisConstants {

    public static final String LOGIN_CODE_KEY = "login:userId:";
    public static final Long LOGIN_CODE_TTL_MINUTES = 30L;

    public static final String SCENCY_CODE_KEY = "scency:Id:";
    public static final Long SCENCY_CODE_TTL_MINUTES = 30L + new Random().nextInt(6);

    public static final String LOCK_CODE_KEY = "lock:Id:";
    public static final Long LOCK_CODE_TTL_SECONDS = 10L;

    public static final String SCENCY_LIKED_KEY = "scency:likeId:";

    public static final Long NULL_CODE_TTL_MINUTES = 2L;

    public static final Long PHONE_CODE_TTL_MINUTES = 1L;
}
