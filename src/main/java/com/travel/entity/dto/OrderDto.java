package com.travel.entity.dto;

import com.travel.entity.Order;
import lombok.Data;
import lombok.EqualsAndHashCode;

/*
 *@ClassName OrderDto 订单中间实体类
 *@Author Freie  stellen
 *@Date 2024/5/23 18:07
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OrderDto extends Order {


//    private List<OrderDetail> list = new ArrayList<>();

    private Long travelId;

    private boolean flag;


}
