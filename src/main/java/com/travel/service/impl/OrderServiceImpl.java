package com.travel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.CommonHolder;
import com.travel.common.ResponseResult;
import com.travel.entity.Order;
import com.travel.entity.OrderTraveler;
import com.travel.entity.Package;
import com.travel.entity.Scency;
import com.travel.entity.dto.TravelerDto;
import com.travel.entity.vo.UserOrderVo;
import com.travel.mapper.OrderMapper;
import com.travel.service.OrderService;
import com.travel.service.OrderTravelerService;
import com.travel.service.PackageService;
import com.travel.service.ScencyService;
import com.travel.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.travel.utils.RedisConstants.TRAVELER_KEY;

/*
 *@ClassName OrderServiceImpl
 *@Author Freie  stellen
 *@Date 2024/5/23 17:54
 */
@Slf4j
@Service
@Transactional
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {


    @Resource
    private RedisCache redisCache;

    @Resource
    private OrderTravelerService orderTravelerService;

    @Lazy
    @Resource
    private ScencyService scencyService;

    @Lazy
    @Resource
    private PackageService packageService;


    /**
     * @Description: 新增订单
     * @param: orderDto
     * @date: 2024/5/23 18:09
     */

    @Override
    public ResponseResult<String> add(Order order) {

        log.info("新增订单{}", order.toString());

        order.setUserId(CommonHolder.getUser());
        order.setStatus(0);

        boolean save = save(order);

        if (!save) {
            return ResponseResult.error("添加失败！");
        }
        Map<String, TravelerDto> map = redisCache.getCacheMap(TRAVELER_KEY);

        ArrayList<OrderTraveler> list = new ArrayList<>();
        for (Map.Entry<String, TravelerDto> map1 : map.entrySet()) {
            OrderTraveler traveler = new OrderTraveler();
            TravelerDto value = map1.getValue();
            traveler.setTravelerName(value.getName());
            traveler.setTravelerNumber(value.getNumber());
            traveler.setTravelerPhone(value.getPhone());
            traveler.setOrderId(order.getId());
            list.add(traveler);
        }
        boolean batch = orderTravelerService.saveBatch(list);

        redisCache.deleteObject(TRAVELER_KEY);
        if (!batch) {
            return ResponseResult.error("添加失败！");
        }
        return ResponseResult.success("添加成功！");
    }

    @Override
    public ResponseResult<String> addTraveler(TravelerDto travelerDto) {

        log.info("新增旅者{}", travelerDto);

        HashMap<String, TravelerDto> map = new HashMap<>();
        map.put(travelerDto.getNumber(), travelerDto);

        redisCache.setCacheMap(TRAVELER_KEY, map);

        return ResponseResult.success("添加旅者成功！");
    }

    @Override
    public ResponseResult<List<UserOrderVo>> selectUserOrder() {

        List<UserOrderVo> collect = lambdaQuery().eq(Order::getUserId, CommonHolder.getUser()).list()
                .stream().map(res -> {
                    UserOrderVo orderVo = new UserOrderVo();
                    orderVo.setId(res.getId().toString());
                    orderVo.setPrice(res.getGrade());
                    orderVo.setTime(res.getCreateTime().toString());
                    orderVo.setStatus(res.getStatus());

                    if (res.getScencyId() == null) {
                        Package one = packageService.lambdaQuery().eq(Package::getId, res.getPackageId())
                                .one();
                        orderVo.setName(one.getName());
                    } else {
                        Scency one = scencyService.lambdaQuery().eq(Scency::getId, res.getScencyId())
                                .one();
                        orderVo.setName(one.getName());
                    }
                    List<TravelerDto> list = orderTravelerService.lambdaQuery().eq(OrderTraveler::getOrderId, res.getId())
                            .list().stream().map(var -> {
                                TravelerDto travelerDto = new TravelerDto();
                                travelerDto.setName(var.getTravelerName());
                                travelerDto.setNumber(var.getTravelerNumber());
                                travelerDto.setPhone(var.getTravelerPhone());

                                return travelerDto;
                            }).collect(Collectors.toList());

                    orderVo.setTraveler(list);
                    return orderVo;
                }).collect(Collectors.toList());

        return ResponseResult.success(collect);
    }
}
