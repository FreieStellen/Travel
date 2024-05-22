package com.travel.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
 *@ClassName PackageOrder 套餐订单表
 *@Author Freie  stellen
 *@Date 2024/4/1 12:02
 */
@Data
public class PackageOrder implements Serializable {

    //解决反序列化兼容问题
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long Id;


    /**
     * 订单号
     */
    private Long orderNumber;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 套餐id
     */
    private Long pOScencyId;

    /**
     * 同行人姓名
     */
    private String pOTravelerName;

    /**
     * 同行人电话
     */
    private String pOTravelerPhone;

    /**
     * 同行人身份证号
     */
    private String pOTravelerNumber;

    /**
     * 订单价格
     */
    private BigDecimal pOGrade;

    /**
     * 状态 0未完成，1已完成
     */
    private int pOType;

    /**
     * 订单创建时间
     */
    @TableField(fill = FieldFill.INSERT, value = "p_o_create_time")
    private LocalDateTime createTime;
}
