package com.travel.entity.vo;

import lombok.Data;

/*
 *@ClassName LoginUserVo
 *@Author Freie  stellen
 *@Date 2024/6/3 22:30
 */
@Data
public class LoginUserVo {

    private String token;
    private String id;
    private String name;
    private String avatar;
}
