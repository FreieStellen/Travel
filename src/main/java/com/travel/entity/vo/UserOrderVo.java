package com.travel.entity.vo;

import com.travel.entity.dto.TravelerDto;
import lombok.Data;

import java.util.List;

/*
 *@ClassName UserOrderVo
 *@Author Freie  stellen
 *@Date 2024/6/16 21:31
 */
@Data
public class UserOrderVo {

    private String id;
    private String name;
    private List<TravelerDto> traveler;
    private String time;
    private int status;
    private String price;
}
