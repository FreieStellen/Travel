package com.travel.utils;

import java.util.regex.Pattern;

/*
 *@ClassName RegexPatterns 正则表达式校验
 *@Author Freie  stellen
 *@Date 2024/3/28 10:19
 */
public abstract class RegexPatterns {
    /**
     * 手机号正则
     */

    public static final Pattern PHONE_REGEX = Pattern.compile("^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\\d{8}$");
    /**
     * 身份证正则
     */

    public static final Pattern NUMBER_REGEX = Pattern.compile("^[1-9]\\d{5}(19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[Xx\\d]$");
    /**
     * 验证码正则, 6位数字或字母
     */

    public static final Pattern VERIFY_CODE_REGEX = Pattern.compile("\\d{6}");
    /**
     * 密码正则, 大于6位且小于20位且必须包含其中两种（数字或字母或符号）
     */

    public static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-zA-Z!@#$%^&*()-+=])(?=\\S+$).{6,20}$";
}
