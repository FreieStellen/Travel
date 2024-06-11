package com.travel.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
 *@ClassName Scency 景点表
 *@Author Freie  stellen
 *@Date 2024/3/22 17:59
 */
@Data
public class Scency {

    //解决反序列化兼容问题
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 分类id
     */
    private Long districtId;

    /**
     * 景点名称
     */
    private String name;

    /**
     * 景点地址
     */
    private String address;

    /**
     * 景点评分
     */
    @TableField(exist = false)
    private String score;

    /**
     * 景点门票价格
     */
    private BigDecimal ticketGrade;

    /**
     * 点赞数量
     */
    private int liked;

    /**
     * 景点门票数量
     */
    private Long ticketNum;

    /**
     * 景点图片
     */
    private String images;

    /**
     * 展示图片
     */
    private String image;

    /**
     * 景点描述
     */
    private String description;

    /**
     * 开放时间
     */
    private String openHours;

    /**
     * 状态 0开放，1关闭,-1下架
     */
    private int status;


    /**
     * 是否点赞
     */
    @TableField(exist = false)
    private boolean isLike;

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
