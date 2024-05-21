package com.travel.utils;

/*
 *@ClassName CommonHolder ThreadLocal线程
 *@Author Freie  stellen
 *@Date 2024/5/8 17:14
 */
public class CommonHolder {
    private static final ThreadLocal<String> tl = new ThreadLocal<>();

    public static void saveUser(String id) {
        tl.set(id);
    }

    public static String getUser() {
        return tl.get();
    }

    public static void removeUser() {
        tl.remove();
    }
}
