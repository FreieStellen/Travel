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

    //*****************************************登录*************************************************

    public static final String LOGIN_CODE_KEY = "login:userId:";
    public static final Long LOGIN_CODE_TTL_MINUTES = 30L;

    //*****************************************登录回显**********************************************

    public static final String USER_NAME_PASS_ECHO_KEY = "user:echo name:";
    public static final Long USER_NAME_PASS_ECHO_TTL_DAYS = 7L;

    //*****************************************用户名查重********************************************

    public static final String USER_NAME_KEY = "user:name:";

    //*****************************************景点套餐**********************************************

    public static final String SCENCY_CODE_KEY = "scency:Id:";
    public static final Long SCENCY_CODE_TTL_MINUTES = 30L + new Random().nextInt(6);

    public static final String SCENCY_POPULAR_KEY = "popular:scency:";
    public static final Long POPULAR_TTL_DAY = 1L;
    public static final String PACKAGE_POPULAR_KEY = "popular:package:";

    public static final String PACKAGE_CODE_KEY = "package:Id:";
    public static final Long PACKAGE_CODE_TTL_MINUTES = 30L + new Random().nextInt(6);

    public static final String SCENCY_LIKED_KEY = "scency:likeId:";
    public static final String PACKAGE_LIKED_KEY = "package:likeId:";

    //*****************************************互斥锁************************************************

    public static final String LOCK_CODE_KEY = "lock:Id:";
    public static final Long LOCK_CODE_TTL_SECONDS = 10L;
    public static final String LOCK_CODE_POPULAR_KEY = "lock:";

    //*****************************************其他*************************************************

    public static final Long NULL_CODE_TTL_MINUTES = 2L;

    public static final Long PHONE_CODE_TTL_MINUTES = 1L;


}
