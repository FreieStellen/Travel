package com.travel.utils;

import com.travel.entity.dto.CommonDto;

/*
 *@ClassName CommonHolder ThreadLocal
 *@Author Freie  stellen
 *@Date 2024/5/8 17:14
 */
public class CommonHolder {
    private static final ThreadLocal<CommonDto> tl = new ThreadLocal<>();

    public static void saveUser(CommonDto commonDto) {
        tl.set(commonDto);
    }

    public static CommonDto getUser() {
        return tl.get();
    }

    public static void removeUser() {
        tl.remove();
    }
}
