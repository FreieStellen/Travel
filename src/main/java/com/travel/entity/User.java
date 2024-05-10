package com.travel.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonGetter;
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
    private Long uId;


    /**
     * 密码
     */
    private String uPassword;

    /**
     * 账号
     */
    private String uAccountId;

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

    @JsonGetter("uId")
    public Long getuId() {
        return uId;
    }

    @JsonGetter("uPassword")
    public String getuPassword() {
        return uPassword;
    }

    @JsonGetter("uAccountId")
    public String getuAccountId() {
        return uAccountId;
    }

    @JsonGetter("uName")
    public String getuName() {
        return uName;
    }

    @JsonGetter("uPhone")
    public String getuPhone() {
        return uPhone;
    }

    @JsonGetter("uNumber")
    public String getuNumber() {
        return uNumber;
    }

    @JsonGetter("uAccount")
    public BigDecimal getuAccount() {
        return uAccount;
    }

    @JsonGetter("uStatus")
    public int getuStatus() {
        return uStatus;
    }
}
