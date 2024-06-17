package com.travel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.CommonHolder;
import com.travel.common.ResponseResult;
import com.travel.entity.Review;
import com.travel.entity.User;
import com.travel.entity.vo.RecoverVo;
import com.travel.entity.vo.ReviewVo;
import com.travel.entity.vo.UserReviewVo;
import com.travel.mapper.ReviewMapper;
import com.travel.service.ReviewService;
import com.travel.service.UserService;
import com.travel.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.travel.utils.RedisConstants.REVIEW_CODE_KEY;
import static com.travel.utils.RedisConstants.REVIEW_TTL_DAYS;

/*
 *@ClassName ReviewServiceImpl
 *@Author Freie  stellen
 *@Date 2024/5/28 17:15
 */
@Slf4j
@Transactional
@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {


    @Resource
    private RedisCache redisCache;

    @Lazy
    @Resource
    private UserService userService;

    /**
     * @Description: 添加评论
     * @param: review
     * @date: 2024/6/7 14:49
     */

    @Override
    public ResponseResult<Review> add(Review review) {

        log.info("拿到的评论是：{}", review);

        //1.添加数据库
        review.setUserId(Long.valueOf(CommonHolder.getUser()));
        boolean save = save(review);

        //1.1判断数据库是否添加成功
        if (!save) {
            //1.2失败则返回失败信息
            return ResponseResult.error("添加失败！");

        }
        //2.成功就删除缓存重建缓存
        Long id = review.getScencyId() == null ? review.getPackageId() : review.getScencyId();
        String key = REVIEW_CODE_KEY + id;
        redisCache.deleteObject(key);

        //2.1查询数据库
        List<ReviewVo> collect = lambdaQuery()
                .eq(review.getScencyId() != null, Review::getScencyId, review.getScencyId())
                .eq(review.getScencyId() == null, Review::getPackageId, review.getPackageId())
                .isNull(Review::getBelongId)
                .list().stream().map(res ->
                {
                    ReviewVo reviewVo = new ReviewVo();
                    reviewVo.setId(res.getId().toString());
                    reviewVo.setContent(res.getContent());
                    reviewVo.setScore(String.valueOf(res.getScore()));
                    userService.lambdaQuery().eq(User::getId, res.getUserId()).list().forEach(var -> {
                        //2.1.1需要封装vo将用户的账户和头像封装
                        reviewVo.setName(var.getAccountId());
                        reviewVo.setAvatar(var.getAvatar());
                    });
                    //2.1.2封装子评论
                    List<RecoverVo> collect1 = lambdaQuery()
                            .eq(review.getScencyId() != null, Review::getScencyId, review.getScencyId())
                            .eq(review.getScencyId() == null, Review::getPackageId, review.getPackageId())
                            .eq(Review::getScore, 0.0)
                            .eq(Review::getBelongId, res.getId())
                            .list().stream().map(jt -> {
                                RecoverVo recoverVo = new RecoverVo();
                                recoverVo.setContent(jt.getContent());
                                userService.lambdaQuery().eq(User::getId, jt.getUserId()).list().forEach(skt -> {
                                    recoverVo.setName(skt.getAccountId());
                                    recoverVo.setAvatar(skt.getAvatar());
                                });
                                return recoverVo;
                            }).collect(Collectors.toList());
                    reviewVo.setRecover(collect1);
                    return reviewVo;
                })
                .collect(Collectors.toList());

        System.out.println(collect);
        //2.2刷新redis
        redisCache.setCacheList(key, collect);
        redisCache.expire(key, REVIEW_TTL_DAYS, TimeUnit.DAYS);
        return ResponseResult.success(review);
    }

    @Override
    public ResponseResult<List<UserReviewVo>> selectReview() {

        List<UserReviewVo> collect = lambdaQuery().eq(Review::getUserId, CommonHolder.getUser()).list()
                .stream().map(res -> {
                    UserReviewVo userReviewVo = new UserReviewVo();
                    userReviewVo.setTime(res.getCreateTime().toString());
                    userReviewVo.setContent(res.getContent());
                    return userReviewVo;
                }).collect(Collectors.toList());

        return ResponseResult.success(collect);
    }
}
