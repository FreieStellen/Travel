package com.travel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.common.ResponseResult;
import com.travel.entity.Order;
import com.travel.entity.dto.TravelerDto;
import com.travel.entity.vo.UserOrderVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService extends IService<Order> {
    ResponseResult<String> add(Order order);

    ResponseResult<String> addTraveler(TravelerDto travelerDto);

    ResponseResult<List<UserOrderVo>> selectUserOrder();

    ResponseResult<List<TravelerDto>> enchoTraveler();

    ResponseResult<String> updateTraveler(TravelerDto travelerDto);
}
