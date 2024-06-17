package com.travel.entity.vo;

import lombok.Data;

import java.util.List;

/*
 *@ClassName ReviewVo
 *@Author Freie  stellen
 *@Date 2024/6/8 23:49
 */
@Data
public class ReviewVo {


    private String id;

    private String name;

    private String avatar;

    private String content;

    private String score;

    private List<RecoverVo> recover;

    private boolean show = false;
}
