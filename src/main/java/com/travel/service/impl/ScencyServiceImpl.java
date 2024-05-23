package com.travel.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.ResponseResult;
import com.travel.entity.Scency;
import com.travel.mapper.ScencyMapper;
import com.travel.service.ScencyService;
import com.travel.service.UserCollectService;
import com.travel.utils.CacheClient;
import com.travel.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.travel.utils.RedisConstants.*;

/*
 *@ClassName ScencyServiceImpl 景点功能实现类
 *@Author Freie  stellen
 *@Date 2024/5/13 18:17
 */
@Slf4j
@Service
public class ScencyServiceImpl extends ServiceImpl<ScencyMapper, Scency> implements ScencyService {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private CacheClient cacheClient;

    @Autowired
    private UserCollectService userCollectService;


    /**
     * @Description: 添加景点
     * @param: scency
     * @date: 2024/5/13 19:46
     */

    @Override
    public ResponseResult<String> add(Scency scency) {

        //管理员添加景点信息到数据库
        boolean save = this.save(scency);

        if (!save) {
            return ResponseResult.error("添加失败！");
        }

        //拿到景点id作为key存到redis中
        Long id = scency.getId();
        String key = SCENCY_CODE_KEY + id;

        //将景点对象转化为map对象
        Map<String, Object> map = BeanUtil.beanToMap(scency);
        log.info("景点集合:{}", map);

        //将景点信息缓存到redis中
        //缓存时间加上1-6的随机数解决缓存雪崩问题
        redisCache.setCacheMap(key, map);
        redisCache.expire(key, SCENCY_CODE_TTL_MINUTES, TimeUnit.MINUTES);

        return ResponseResult.success("添加成功！");
    }

    /**
     * @Description: 根据id查询
     * @param: id
     * @date: 2024/5/13 20:47
     */

    @Override
    public ResponseResult<Scency> selectScencyById(Long id) {

        //解决缓存穿透问题
        Scency scency = cacheClient
                .queryWithPassThrough(SCENCY_CODE_KEY, id, Scency.class, this::getById, SCENCY_CODE_TTL_MINUTES, TimeUnit.MINUTES);


        if (Objects.isNull(scency)) {
            return ResponseResult.error("获取失败");
        }

        //判断是否点赞
        isLike(scency);
        log.info("拿到的景点：{}", scency);
        return ResponseResult.success(scency);
    }

    /**
     * @Description: 是否已点赞
     * @param:
     * @date: 2024/5/20 11:59
     */

    public void isLike(Scency scency) {

        Double like = cacheClient.isLike(SCENCY_LIKED_KEY, scency.getId());
        scency.setLike(like != null);

        log.info("拿到景点：{}", scency);
    }

    /**
     * @Description: 点赞收藏
     * @param: id
     * @date: 2024/5/20 11:44
     */

    @Transactional
    @Override
    public ResponseResult<String> likeScency(Long id) {
//        //1.首先拿到登录用户
//        String userId = CommonHolder.getUser();
//
//        //2.判断是否点赞过
//        //2.1拼接点赞的key
//        String key = SCENCY_LIKED_KEY + id;
//
//        //2.2去redis中获取点赞缓存
//        Double score = redisCache.score(key, userId);
//
//        //2.3构建条件构造器
//        LambdaQueryWrapper<UserCollect> wrapper = new LambdaQueryWrapper<>();
//        if (score == null) {
//            //3.如果未点赞，则可以点赞
//            //3.1数据库中点赞数加1--->update
//            boolean update = update().setSql("liked = liked + 1").eq("id", id).update();
//
//            //3.2用户收藏表中加入个人收藏--->save
//            UserCollect userCollect = new UserCollect();
//            userCollect.setUserId(Long.valueOf(userId));
//            userCollect.setCollectId(id);
//
//            boolean save = userCollectService.save(userCollect);
//
//            if (update && save) {
//                //3.2点赞加1后将数据存到redis中
//                redisCache.add(key, userId, System.currentTimeMillis());
//            }
//        } else {
//            //4.如果点过赞了，就取消点赞
//            //4.1数据库中点赞数减1--->update
//            boolean update = update().setSql("liked = liked - 1").eq("id", id).update();
//
//            //4.2用户收藏表中删除个人收藏--->
//            wrapper.eq(UserCollect::getCollectId, id);
//            boolean remove = userCollectService.remove(wrapper);
//            if (update && remove) {
//                //4.2点赞减一后删除redis中的缓存
//                redisCache.remove(key, userId);
//            }
//        }
//        return ResponseResult.success("点赞收藏操作！");

        boolean like = cacheClient.like(SCENCY_LIKED_KEY, id, Scency.class);

        if (like) {
            return ResponseResult.success("点赞收藏景点操作成功！");
        }
        return ResponseResult.error("点赞收藏景点操作失败！");
    }
}
