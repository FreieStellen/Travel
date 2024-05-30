package com.travel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.entity.Review;
import com.travel.mapper.ReviewMapper;
import com.travel.service.ReviewService;
import org.springframework.stereotype.Service;

/*
 *@ClassName ReviewServiceImpl
 *@Author Freie  stellen
 *@Date 2024/5/28 17:15
 */
@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {
}
