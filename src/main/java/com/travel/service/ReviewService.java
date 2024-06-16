package com.travel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.common.ResponseResult;
import com.travel.entity.Review;
import com.travel.entity.vo.UserReviewVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ReviewService extends IService<Review> {
    ResponseResult<Review> add(Review review);

    ResponseResult<List<UserReviewVo>> selectReview();
}
