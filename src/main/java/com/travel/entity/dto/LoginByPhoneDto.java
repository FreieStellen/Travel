package com.travel.entity.vo;

import lombok.Data;

/*
 *@ClassName ManagerVo web向管理员和用户渲染数据时所用的类
 *@Author Freie  stellen
 *@Date 2024/3/31 21:13
 */
@Data
public class LoginByPhoneVo {

    private String code;
    private String phone;
}
