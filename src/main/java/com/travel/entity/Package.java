package com.travel.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
 *@ClassName Package 套餐表
 *@Author Freie  stellen
 *@Date 2024/5/21 16:50
 */
@Data
public class Package {

    //解决反序列化兼容问题
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 套餐名称
     */
    private String name;

    /**
     * 套餐特点
     */
    private String special;

    /**
     * 套餐价格
     */
    private BigDecimal grade;

    /**
     * 套餐描述
     */
    private String discribe;

    /**
     * 套餐数量
     */
    private int ticketNum;

    /**
     * 点赞数量
     */
    private int liked;

    /**
     * 评分
     */
    @TableField(exist = false)
    private String score;

    /**
     * 费用描述
     */
    private String gradeDiscribe;


    /**
     * 状态 0上架，1下架
     */
    private int status;

    /**
     * 是否点赞
     */
    @TableField(exist = false)
    private boolean isLike;

    /**
     * 出发时间
     */
    private LocalDateTime predetermineTime;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
