package com.travel.controller;

import com.travel.common.ResponseResult;
import com.travel.entity.Review;
import com.travel.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/*
 *@ClassName ReviewController
 *@Author Freie  stellen
 *@Date 2024/6/7 14:38
 */
@Slf4j
@RestController
@RequestMapping("/review")
public class ReviewController {

    @Resource
    private ReviewService reviewService;

    /**
     * @Description: 添加评论
     * @param: review
     * @date: 2024/6/7 14:49
     */

    @PostMapping("/add")
    public ResponseResult<Review> add(@RequestBody Review review) {

        return reviewService.add(review);
    }
}
