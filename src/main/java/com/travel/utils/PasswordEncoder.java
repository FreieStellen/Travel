package com.travel.utils;

import cn.hutool.core.util.RandomUtil;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/*
 *@ClassName PasswordEncoder //密码加密类
 *@Author Freie  stellen
 *@Date 2024/5/10 19:39
 */
public class PasswordEncoder {

    public static String encode(String password) {
        // 生成盐
        String salt = RandomUtil.randomString(20);
        // 加密
        return encode(password, salt);
    }

    private static String encode(String password, String salt) {
        // 加密
        return "@XWX" + "-" + salt + "-" + DigestUtils.md5DigestAsHex((password + salt).getBytes(StandardCharsets.UTF_8));
    }

    public static Boolean matches(String encodedPassword, String rawPassword) {
        if (encodedPassword == null || rawPassword == null) {
            return false;
        }
        if (!encodedPassword.contains("@XWX")) {
            throw new RuntimeException("密码格式不正确！");
        }
        String[] arr = encodedPassword.split("-");
        // 获取盐
        String salt = arr[1];
        // 比较
        return encodedPassword.equals(encode(rawPassword, salt));
    }
}
