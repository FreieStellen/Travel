package com.travel.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * @Description: JWT的工具类
 * @param: null
 * @date: 2024/3/27 10:33
 */

public class JwtUtil {

    //设置JWT的过期时间
    public static final Long JWT_TTL = 60 * 60 * 1000L;// 60 * 60 *1000  一个小时
    //设置秘钥明文
    public static final String JWT_KEY = "XXW";

    /**
     * @Description: 随机生成UUID
     * @param:
     * @date: 2024/3/27 10:42
     */

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * @Description: 生成JWT（一参：数据JSON格式）
     * @param: subject
     * @date: 2024/3/27 10:39
     */

    public static String createJWT(String subject) {
        JwtBuilder builder = getJwtBuilder(subject, null, getUUID());
        return builder.compact();
    }

    /**
     * @Description: 生成JWT（两参：数据和过期时间）
     * @param: subject ttlMillis
     * @date: 2024/3/27 10:40
     */

    public static String createJWT(String subject, Long ttlMillis) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, getUUID());
        return builder.compact();
    }

    /**
     * @Description: 创建token
     * @param: id subject ttlMillis
     * @date: 2024/3/27 11:04
     */

    public static String createJWT(String id, String subject, Long ttlMillis) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, id);
        return builder.compact();
    }

    /**
     * @Description: 构建JWT
     * @param: subject ttlMillis uuid
     * @date: 2024/3/27 10:54
     */

    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid) {

        //使用HS256对称加密算法签名
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        //生成密钥
        SecretKey secretKey = generalKey();

        //获取本地时间
        long nowMillis = System.currentTimeMillis();

        //设置签发时间
        Date now = new Date(nowMillis);

        //设置过期时间
        if (ttlMillis == null) {
            ttlMillis = JwtUtil.JWT_TTL;
        }

        //设置令牌有效时间
        long expMillis = nowMillis + ttlMillis;

        Date expDate = new Date(expMillis);
        return Jwts.builder()
                .setId(uuid)              //唯一的ID
                .setSubject(subject)   // 主题  可以是JSON数据
                .setIssuer("sg")     // 签发者
                .setIssuedAt(now)      // 签发时间
                .signWith(signatureAlgorithm, secretKey) //使用HS256对称加密算法签名, 第二个参数为秘钥
                .setExpiration(expDate);  // 过期时间
    }

    /**
     * @Description: 生成加密后的密钥
     * @param:
     * @date: 2024/3/27 11:04
     */

    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.getDecoder().decode(JwtUtil.JWT_KEY);
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }

    /**
     * @Description: 解析JWT
     * @param: jwt
     * @date: 2024/3/27 11:05
     */

    public static Claims parseJWT(String jwt) {
        SecretKey secretKey = generalKey();
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();
    }


}