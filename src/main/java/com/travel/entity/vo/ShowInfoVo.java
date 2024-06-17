package com.travel.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/*
 *@ClassName ShowInfoVo
 *@Author Freie  stellen
 *@Date 2024/6/6 14:29
 */
@Data
public class ShowInfoVo {

    //套餐表的主键id
    private String id;

    //景点表的地区id
    private String districtId;

    //套餐的地区名称集合
    private List<Object> districtList;

    private String name;

    private String address;

    private String description;

    private BigDecimal ticketGrade;

    private int liked;

    private String reviewed;

    private String score;

    private Long ticketNum;

    //景点的地区名称
    private String district;

    private String images;

    //景点的
    private String openHours;

    //套餐表的出发时间
    private String predetermineTime;

    private List<ReviewVo> reviews;

    //是否点赞
    private boolean isLike;

    //套餐特点
    private String special;

    //价格特点
    private String gradeDiscribe;

}
