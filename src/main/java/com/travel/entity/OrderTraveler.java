package com.travel.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/*
 *@ClassName OrderTraveler
 *@Author Freie  stellen
 *@Date 2024/5/28 17:33
 */
@Data
public class OrderTraveler {

    //解决反序列化兼容问题
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 旅者姓名
     */
    private String travelerName;

    /**
     * 旅者电话
     */
    private String travelerPhone;

    /**
     * 旅者身份证号
     */
    private String travelerNumber;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
