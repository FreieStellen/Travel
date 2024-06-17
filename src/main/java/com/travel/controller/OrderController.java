package com.travel.controller;

import com.travel.common.ResponseResult;
import com.travel.entity.Order;
import com.travel.entity.dto.TravelerDto;
import com.travel.entity.vo.UserOrderVo;
import com.travel.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * @Description: 添加订单
     * @param: order
     * @date: 2024/6/16 21:56
     */

    @PostMapping("/add")
    public ResponseResult<String> add(@RequestBody Order order) {


        log.info(order.toString());
        return orderService.add(order);
    }

    /**
     * @Description: 添加出行人
     * @param: travelerDto
     * @date: 2024/6/16 21:56
     */

    @PostMapping("/traveler")
    public ResponseResult<String> addTraveler(@RequestBody TravelerDto travelerDto) {

        log.info(travelerDto.toString());
        return orderService.addTraveler(travelerDto);
    }

    /**
     * @Description: 查看个人订单
     * @param:
     * @date: 2024/6/16 21:56
     */

    @GetMapping("/userorder")
    public ResponseResult<List<UserOrderVo>> selectUserOrder() {

        return orderService.selectUserOrder();
    }

    /**
     * @Description: 回显出行人
     * @param:
     * @date: 2024/6/17 20:05
     */

    @GetMapping("/encho")
    public ResponseResult<List<TravelerDto>> enchoTraveler() {
        return orderService.enchoTraveler();
    }

    /**
     * @Description: 修改旅者信息
     * @param: travelerDto
     * @date: 2024/6/17 20:40
     */

    @PostMapping("/update")
    public ResponseResult<String> updateTraveler(@RequestBody TravelerDto travelerDto) {

        return orderService.updateTraveler(travelerDto);
    }
}
