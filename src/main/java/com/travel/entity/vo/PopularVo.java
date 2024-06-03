package com.travel.entity.vo;

import lombok.Data;

/*
 *@ClassName PopularVo
 *@Author Freie  stellen
 *@Date 2024/6/1 15:50
 */
@Data
public class PopularVo {

    private Long id;
    private String name;
    private String address;
    private String discribe;
    private String image;
    private int liked;

}
