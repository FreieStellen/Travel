package com.travel.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/*
 *@ClassName User 用户表
 *@Author Freie  stellen
 *@Date 2024/3/22 17:20
 */
@Data
public class User implements Serializable {

    //解决反序列化兼容问题
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long uId;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 密码
     */
    private String uPassword;

    /**
     * 账户
     */
    private String uAccountid;

    /**
     * 姓名
     */
    private String uName;

    /**
     * 手机号
     */
    private String uPhone;

    /**
     * 性别
     */
    private String sex;

    /**
     * 身份证号
     */
    private String uNumber;

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
    private BigDecimal uAccount;

    /**
     * 状态 0禁用，1正常
     */
    private int uStatus;
}
