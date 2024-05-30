package com.travel.entity.dto;

import lombok.Data;

/*
 *@ClassName LoginByIdVo
 *@Author Freie  stellen
 *@Date 2024/4/3 11:07
 */
@Data
public class LoginByIdDto {

    private String username;
    private String password;


    //判断是否记住密码
    private boolean flag;
}
