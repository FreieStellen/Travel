package com.travel.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/*
 *@ClassName User 用户表
 *@Author Freie  stellen
 *@Date 2024/3/22 17:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    //解决反序列化兼容问题
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private Long id;


    /**
     * 密码
     */
    private String password;

    /**
     * 账号
     */
    private String accountId;


    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 性别
     */
    private String sex;

    /**
     * 身份证号
     */
    private String number;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 是否为vip 0:非会员，1：会员
     */
    private int isvip;

    /**
     * 优惠金
     */
    private BigDecimal discount;

    /**
     * 账户余额
     */
    private BigDecimal account;

    /**
     * 状态 0禁用，1正常
     */
    private int status;


}
