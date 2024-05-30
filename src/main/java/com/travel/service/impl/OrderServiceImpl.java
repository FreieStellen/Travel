package com.travel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.ResponseResult;
import com.travel.entity.Order;
import com.travel.entity.dto.OrderDto;
import com.travel.mapper.OrderMapper;
import com.travel.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 *@ClassName OrderServiceImpl
 *@Author Freie  stellen
 *@Date 2024/5/23 17:54
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {


//    @Autowired
//    private OrderDetailService orderDetailService;

    /**
     * @Description: 新增订单
     * @param: orderDto
     * @date: 2024/5/23 18:09
     */

    @Transactional
    @Override
    public ResponseResult<String> add(OrderDto orderDto) {
//        //1.判断有无填写其他游客
//        //1.1获取游客信息
//        String travelerPhone = orderDto.getTravelerPhone();
//        String travelerNumber = orderDto.getTravelerNumber();
//        String travelerName = orderDto.getTravelerName();
//
//        //1.2判断是否存在其他游客
//        if (travelerNumber != null && travelerName != null && travelerPhone != null) {
//            //2.对游客信息进行校验
//            //2.1校验身份证号
//            if (!RegexUtil.isNumberInvalid(travelerNumber)) {
//                return ResponseResult.error("旅客身份证号码填写有误");
//            }
//            //2.2校验手机号
//            if (RegexUtil.isPhoneInvalid(travelerPhone)) {
//                return ResponseResult.error("旅客手机号码填写有误");
//            }
//        }
//        //3.取到用户id
//        String userId = CommonHolder.getUser();
//        if (StrUtil.isBlank(userId)) {
//            return ResponseResult.error("未登录！");
//        }
//        orderDto.setUserId(Long.valueOf(userId));
//        //4.添加订单表
//        boolean save = save(orderDto);
//        if (!save) {
//            return ResponseResult.error("订单提交失败");
//        }
//
//        //4.提取订单id添加到订单（景点/套餐）表
//        List<OrderDetail> list = orderDto.getList();
//        List<OrderDetail> collect = list.stream().peek(var -> {
//
//            //4.1判断订单是套餐订单还是景点订单
//            boolean flag = orderDto.isFlag();
//            //4.2取出id
//            Long travelId = orderDto.getTravelId();
//            //4.3设置订单id
//            var.setOrderId(orderDto.getId());
//            if (flag) {
//                //4.4true就添加景点id
//                var.setScencyId(travelId);
//            } else {
//                //4.5false添加套餐id
//                var.setPackageId(travelId);
//            }
//        }).collect(Collectors.toList());
//
//        //5.添加订单（景点/套餐）表
//        boolean batch = orderDetailService.saveBatch(collect);
//
//        if (batch) {
//            return ResponseResult.success("添加成功！");
//        }
        return ResponseResult.error("添加失败！");
    }
}
