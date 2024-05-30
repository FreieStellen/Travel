package com.travel.controller;

import com.travel.common.ResponseResult;
import com.travel.entity.dto.OrderDto;
import com.travel.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 *@ClassName OrderController
 *@Author Freie  stellen
 *@Date 2024/5/23 17:55
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/add")
    public ResponseResult<String> add(@RequestBody OrderDto orderDto) {


        log.info(orderDto.toString());
        return orderService.add(orderDto);
    }
}
