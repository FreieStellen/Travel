package com.travel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.common.ResponseResult;
import com.travel.entity.Order;
import com.travel.entity.dto.OrderDto;
import org.springframework.stereotype.Service;

@Service
public interface OrderService extends IService<Order> {
    ResponseResult<String> add(OrderDto orderDto);
}
