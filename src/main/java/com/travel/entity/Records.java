package com.travel.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/*
 *@ClassName Records 日志记录表
 *@Author Freie  stellen
 *@Date 2024/5/28 17:17
 */
@Data
public class Records {
    //解决反序列化兼容问题
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 管理员id
     */
    @TableField(fill = FieldFill.INSERT)
    private String managerId;

    /**
     * 日志内容
     */
    private String content;


    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
