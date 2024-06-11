package com.travel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.common.ResponseResult;
import com.travel.entity.Review;
import org.springframework.stereotype.Service;

@Service
public interface ReviewService extends IService<Review> {
    ResponseResult<Review> add(Review review);

}
