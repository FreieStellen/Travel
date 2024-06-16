package com.travel.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/*
 *@ClassName order
 *@Author Freie  stellen
 *@Date 2024/5/23 17:44
 */
@Data
@TableName("orders")
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
    private String userId;

    /**
     * 景点id
     */
    private String scencyId;

    /**
     * 套餐id
     */
    private String packageId;

    /**
     * 订单价格
     */
    private String grade;

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
     * 状态 0:待支付，1:已支付,2:已取消，3:已退款，4:已完成
     */
    private int status;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 预定时间
     */
    private String reserveTime;


}
