package com.travel.utils;

import static com.travel.utils.RegexPatterns.*;

/*
 *@ClassName RegexUtil 用户注册校验认证
 *@Author Freie  stellen
 *@Date 2024/3/28 10:21
 */
public class RegexUtil {
    /**
     * 是否是无效用户名
     *
     * @param username 要校验的用户名
     * @return true:符合，false：不符合
     */

    public static boolean isUsernameInvalid(String username) {
        return username != null && username.length() >= 1 && username.length() <= 10;
    }

    /**
     * 是否是无效密码
     *
     * @param password 要校验的密码
     * @return true:符合，false：不符合
     */

    public static boolean isPasswordInvalid(String password) {
        return password != null && PASSWORD_REGEX.matcher(password).matches();
    }

    /**
     * 是否是无效手机格式
     *
     * @param phone 要校验的手机号
     * @return true:符合，false：不符合
     */

    public static boolean isPhoneInvalid(String phone) {
        return phone != null && PHONE_REGEX.matcher(phone).matches();
    }

    /**
     * 是否是无效身份证格式
     *
     * @param number 要校验的身份证
     * @return true:符合，false：不符合
     */

    public static boolean isNumberInvalid(String number) {
        return number != null && NUMBER_REGEX.matcher(number).matches();
    }

    /**
     * 是否是无效验证码格式
     *
     * @param code 要校验的验证码
     * @return true:符合，false：不符合
     */

    public static boolean isCodeInvalid(String code) {
        return VERIFY_CODE_REGEX.matcher(code).matches();
    }


}
