package com.travel.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/*
 *@ClassName Manager 管理员表
 *@Author Freie  stellen
 *@Date 2024/3/22 17:37
 */
@Data
public class Manager implements Serializable {

    //解决反序列化兼容问题
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private Long mId;

    /**
     * 姓名
     */
    private String mName;

    /**
     * 账号
     */
    private String mAccount;

    /**
     * 密码
     */
    private String mPassword;

    /**
     * 手机号
     */
    private String mPhone;

    /**
     * 性别
     */
    private String sex;

    /**
     * 身份证号
     */
    private String mNumber;

    /**
     * 创建时间
     * 插入时填充字段
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 修改时间
     * 插入和修改时填充字段
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    /**
     * 修改人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

    /**
     * 状态 0禁用，1正常
     */
    private int mStatus;
}
