package com.travel.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
 *@ClassName order
 *@Author Freie  stellen
 *@Date 2024/5/23 17:44
 */
@Data
public class Order {

    //解决反序列化兼容问题
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 景点id
     */
    private Long scencyId;

    /**
     * 套餐id
     */
    private Long packageId;

    /**
     * 订单价格
     */
    private BigDecimal grade;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 退款时间
     */
    private LocalDateTime refundTime;

    /**
     * 状态 0:待支付，1:已支付,2:已取消，3:已退款
     */
    private int status;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;
}
