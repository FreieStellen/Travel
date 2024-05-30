package com.travel.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/*
 *@ClassName Review 评论表
 *@Author Freie  stellen
 *@Date 2024/5/28 17:10
 */
@Data
public class Review {

    //解决反序列化兼容问题
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 所属评论id
     */
    private Long belongId;

    /**
     * 景点id
     */
    private Long scencyId;

    /**
     * 套餐id
     */
    private Long packageId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 景点/套餐评分
     */
    private float score;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
