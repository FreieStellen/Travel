package com.travel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.entity.OrderTraveler;
import com.travel.mapper.OrderTravelerMapper;
import com.travel.service.OrderTravelerService;
import org.springframework.stereotype.Service;

/*
 *@ClassName OrderTravelerServiceImpl
 *@Author Freie  stellen
 *@Date 2024/5/28 17:36
 */
@Service
public class OrderTravelerServiceImpl extends ServiceImpl<OrderTravelerMapper, OrderTraveler> implements OrderTravelerService {
}
