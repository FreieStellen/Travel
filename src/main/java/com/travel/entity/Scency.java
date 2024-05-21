package com.travel.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonGetter;
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
    private Long sId;

    /**
     * 分类id
     */
    private Long categoryId;

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
     * 景点门票价格
     */
    private BigDecimal sTicketGrade;

    /**
     * 点赞数量
     */
    private int liked;

    /**
     * 景点门票数量
     */
    private Long sTicketNum;

    /**
     * 景点图片
     */
    private String sImages;

    /**
     * 景点描述
     */
    private String sDiscribe;

    /**
     * 开放时间
     */
    private String openHours;

    /**
     * 状态 0开放，1关闭
     */
    private int sStatus;

    /**
     * 状态 0存在，1删除
     */
    private int sExist;

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

    @JsonGetter("sName")
    public String getsName() {
        return sName;
    }

    public String getsAddress() {
        return sAddress;
    }

    public String getsScore() {
        return sScore;
    }

    public BigDecimal getsTicketGrade() {
        return sTicketGrade;
    }

    public Long getsTicketNum() {
        return sTicketNum;
    }

    public String getsImages() {
        return sImages;
    }

    public String getsDiscribe() {
        return sDiscribe;
    }

    public int getsStatus() {
        return sStatus;
    }

    public int getsExist() {
        return sExist;
    }
}
