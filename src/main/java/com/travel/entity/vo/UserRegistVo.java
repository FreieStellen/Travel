package com.travel.entity.vo;

import com.travel.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

/*
 *@ClassName UserRegistVo 用户注册web向后端渲染数据时所用到的类
 *@Author Freie  stellen
 *@Date 2024/3/31 21:36
 */

@EqualsAndHashCode(callSuper = true)//确保调用父类的equals()和hashCode()方法。
@Data
public class UserRegistVo extends User {

    private Integer code;
}
