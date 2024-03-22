package com.travel.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

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
    private Long sId;

    /**
     * 景点名称
     */
    private String sName;

    /**
     * 景点地址
     */
    private String sAddress;

    /**
     * 景点评分
     */
    private String sScore;

    /**
     * 景点门票
     */
    private BigDecimal sTicketGrade;

    /**
     * 景点门票数量
     */
    private Long sTicketNum;

    /**
     * 景点描述
     */
    private String sDiscribe;

    /**
     * 开放时间
     */
    private Date openTime;

    /**
     * 关闭时间
     */
    private Date closeTime;
    /**
     * 预定时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime sPredetermineTime;

    /**
     * 状态 0开放，1关闭
     */
    private int sStatus;
}
