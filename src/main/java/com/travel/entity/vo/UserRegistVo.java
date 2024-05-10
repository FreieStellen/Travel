package com.travel.entity.vo;

import com.travel.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/*
 *@ClassName UserRegistVo 用户注册web向后端渲染数据时所用到的类
 *@Author Freie  stellen
 *@Date 2024/3/31 21:36
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistVo extends User {

    private Integer code;
}
