package com.travel.utils;

/*
 *@ClassName RedisData 解决缓存击穿问题所需的封装的类
 *@Author Freie  stellen
 *@Date 2024/5/19 10:06
 */

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RedisData {

    //逻辑过期时间
    private LocalDateTime expireTime;

    //数据
    private Object data;
}
