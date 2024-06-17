package com.travel.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.travel.common.CommonHolder;
import com.travel.entity.Package;
import com.travel.entity.*;
import com.travel.entity.vo.*;
import com.travel.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.travel.utils.RedisConstants.*;

/*
 *@ClassName CacheClient 封装景点相关操作类
 *@Author Freie  stellen
 *@Date 2024/5/19 10:14
 */
@Slf4j
@Component
public class CacheClient {

    @Autowired
    private RedisCache redisCache;

    @Lazy
    @Autowired
    private ScencyService scencyService;

    @Lazy
    @Autowired
    private PackageService packageService;

    @Autowired
    private UserCollectService userCollectService;

    @Resource
    private RecordsService recordsService;

    @Resource
    private DataBaseControl dataBaseControl;

    @Lazy
    @Resource
    private DistrictService districtService;

    @Lazy
    @Resource
    private ReviewService reviewService;

    @Lazy
    @Resource
    private PackageDistrictService packageDistrictService;

    @Lazy
    @Resource
    private UserService userService;


    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);


    /**
     * @Description: //设置逻辑过期以redisData封装类的形式存入redis（热门景点）
     * @param: key
     * value
     * time
     * unit
     * @date: 2024/6/6 15:36
     */

    public void setWithLogicalExpire(String key, Object value, Long time, TimeUnit unit) {
        // 设置逻辑过期
        RedisData redisData = new RedisData();
        redisData.setData(value);

        //设置逻辑过期时间
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));
        // 写入Redis
        redisCache.setCacheObject(key, JSONUtil.toJsonStr(redisData));
    }


    /**
     * @Description: 景点%套餐单个查询[解决缓存穿透和缓存击穿问题（使用互斥锁）]
     * @param: keyPrefix, id, type, dbFallback, timeunit
     * @date: 2024/6/6 15:35
     */

    public <R, ID> ShowInfoVo queryWithPassThrough(
            String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit) {

        ShowInfoVo showInfoVo = new ShowInfoVo();
        //1.查找缓存
        //1.1拼接key
        String key = keyPrefix + id;

        boolean flag = type == Scency.class;

        //1.2redis中查找缓存景点
        Map<String, Object> map = redisCache.getCacheMap(key);

        ShowInfoVo back = null;
        //1.3判断是否存在
        if (!map.isEmpty()) {

            if (map.size() == 1) {

                return null;
            }
            log.info("景点/套餐存在Redis中");
            //1.4存在就将map对象转为scency对象返回(isToCamelCase表示是否将 Map 中的下划线命名转换为驼峰命名
            //CopyOptions.create()表示选择性拷贝属性)
            R r = BeanUtil.mapToBean(map, type, false, CopyOptions.create());
            BeanUtil.copyProperties(r, showInfoVo);
            //1.5添加数据到中间类
            //1.6判断类型是否为景点或套餐
            if (flag) {
                String districtId = showInfoVo.getDistrictId().toString();
                back = dbFallBack(districtId, showInfoVo, id.toString());
            } else {
                back = dbFallBack(showInfoVo, id.toString());
            }
            log.info("查询到:{}", back);
            //返回景点信息
            return back;
        }
        //2.不存在就查询数据库
        //2.1拼接锁的key
        String lockKey = LOCK_CODE_KEY + id;
        R r;
        try {
            //2.2不存在去尝试获取锁
            boolean lock = tryLock(lockKey);

            //2.3判断是否获取锁
            if (!lock) {
                //没有获取就让线程等待
                Thread.sleep(50);
                //递归调用
                return queryWithPassThrough(keyPrefix, id, type, dbFallback, time, unit);
            }
            //2.4获取成功后去查询数据库
            //apply方法可以接受一个泛型ID对象返回一个泛型R对象
            r = dbFallback.apply(id);

            //3.判断拿到的对象是否为null
            if (Objects.isNull(r)) {

                Map<String, String> hashMap = new HashMap<>();
                hashMap.put("null", "");
                //3.1为null就在redis中储存空值并设置过期时间解决缓存穿透问题
                redisCache.setCacheMap(key, hashMap);
                redisCache.expire(key, NULL_CODE_TTL_MINUTES, TimeUnit.MINUTES);
                //返回不存在
                return null;
            }
            log.info("景点/套餐不存在Redis中，拿到锁成功！");
            //3.2不为null就返回并且同步数据到缓存中
            BeanUtil.copyProperties(r, showInfoVo);
            Map<String, Object> map1 = BeanUtil.beanToMap(r);

            log.info("swsw{}", showInfoVo);
            //3.3判断类型是否为景点或套餐
            if (flag) {
                String districtId = showInfoVo.getDistrictId().toString();
                back = dbFallBack(districtId, showInfoVo, id.toString());
                log.info("拿到的景点类{}", back);
            } else {
                log.info("swswSSSSSSSSS{}", showInfoVo);
                back = dbFallBack(showInfoVo, id.toString());
                log.info("拿到的套餐类{}", back);
            }
            redisCache.setCacheMap(key, map1);
            redisCache.expire(key, time, unit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //释放锁
            unlock(lockKey);
        }
        return back;
    }

    /**
     * @Description: 套餐数据渲染
     * @param: null
     * @date: 2024/6/8 17:13
     */
    public ShowInfoVo dbFallBack(ShowInfoVo showInfoVo, String packageId) {
        //1查询地区名称--->套餐地区表  查询评论数--->评论表  查询评分--->评论表
        //1.1缓存中查找地区是否存在
        List<Object> redisDistrictName = redisCache.getCacheList(DISTRICT_CODE_KEY + packageId);
        log.info("查询redis地区表(套餐地区)成功！{}", redisDistrictName);
        //1.2缓存中查找对应套餐的评论数
        Long num = redisCache.getCacheObject(REVIEW_NUM_KEY + packageId);
        log.info("查询redis评论表(评论数)成功！{}", num);
        //1.3缓存中查找对应套餐的评分
        Double redisScore = redisCache.getCacheObject(PACKAGE_NUM_KEY + packageId);
        log.info("查询redis评论表(评分)成功！{}", redisScore);
        //1.4缓存中查找对应套餐的评论
        List<ReviewVo> redisList = redisCache.getCacheList(REVIEW_CODE_KEY + packageId);
        log.info("查询redis评论表(评论)成功！{}", redisList);
        //1.5判断地区是否为空
        if (!redisDistrictName.isEmpty()) {
            log.info("套餐信息存在Redis中");
            //1.5不为空则一一赋值
            log.info("赋值中1");
            showInfoVo.setDistrictList(redisDistrictName);
            log.info("赋值中2");
            showInfoVo.setReviewed(String.valueOf(num));
            log.info("赋值中3");
            showInfoVo.setScore(String.valueOf(redisScore));
            log.info("赋值中4");
            showInfoVo.setReviews(redisList);
            log.info("赋值中5");
            //返回
            return showInfoVo;
        }
        //2.为空则加锁去数据库查询
        try {
            boolean lock = tryLock(LOCK_CODE_SHOWING_KEY);
            //2.1判断加锁是否成功
            if (!lock) {
                //2.2拿锁失败就线程休眠1秒
                Thread.sleep(1000);
                return dbFallBack(showInfoVo, packageId);
            }
            //2.3拿锁成功去查询数据库
            //2.3.1查询地区表(套餐地区)并加入缓存
            log.info("套餐信息不存在Redis中，拿到锁成功！");
            List<Object> districtList = packageDistrictService.listObjs(new LambdaQueryWrapper<PackageDistrict>()
                            .eq(PackageDistrict::getPackageId, packageId)
                            .select(PackageDistrict::getDistrictId))
                    .stream().map(res ->
                            districtService.listObjs(new LambdaQueryWrapper<District>()
                                    .eq(District::getId, res).select(District::getName))
                    ).collect(Collectors.toList());
            String listDistrictKey = DISTRICT_CODE_KEY + packageId;
            redisCache.setCacheList(listDistrictKey, districtList);
            redisCache.expire(listDistrictKey, DISTRICT_TTL_DAYS, TimeUnit.DAYS);
            log.info("查询地区表(套餐地区)成功！{}", districtList);

            //2.3.2查询评论表(评论数)并加入缓存
            Long count = reviewService.lambdaQuery().eq(Review::getPackageId, packageId)
                    .isNull(Review::getBelongId)
                    .count();
            redisCache.setCacheObject(REVIEW_NUM_KEY + packageId, count, REVIEW_TTL_DAYS, TimeUnit.DAYS);
            log.info("查询评论表(评论数)成功！{}", count);

            //2.3.3查询评论表(评论)并加入缓存
            List<ReviewVo> collect = reviewService.lambdaQuery()
                    .eq(Review::getPackageId, packageId)
                    .isNull(Review::getBelongId)
                    .list().stream().map(res ->
                    {
                        ReviewVo reviewVo = new ReviewVo();
                        reviewVo.setId(res.getId());
                        reviewVo.setContent(res.getContent());
                        reviewVo.setScore(String.valueOf(res.getScore()));
                        userService.lambdaQuery().eq(User::getId, res.getUserId()).list().forEach(var -> {
                            //2.3.3.1需要封装vo将用户的账户和头像封装
                            reviewVo.setName(var.getAccountId());
                            reviewVo.setAvatar(var.getAvatar());
                        });
                        //2.3.3.2封装子评论
                        List<RecoverVo> collect1 = reviewService.lambdaQuery()
                                .eq(Review::getPackageId, packageId)
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
            String listReviewKey = REVIEW_CODE_KEY + packageId;
            redisCache.setCacheList(listReviewKey, collect);
            redisCache.expire(listReviewKey, REVIEW_TTL_DAYS, TimeUnit.DAYS);
            log.info("查询评论表(评论)成功！{}", collect);

            //2.3.4查询评论表(评分)并加入缓存
            double average = reviewService.getBaseMapper().selectObjs(new LambdaQueryWrapper<Review>()
                            .select(Review::getScore)
                            .eq(Review::getPackageId, packageId)
                            .isNull(Review::getBelongId))
                    .stream().mapToDouble(res -> (float) res).average().orElse(0);
            double score = (Math.round(average * 10));
            score /= 10;
            redisCache.setCacheObject(PACKAGE_NUM_KEY + packageId, score, REVIEW_TTL_DAYS, TimeUnit.DAYS);
            log.info("查询评论表(评分)成功！{}", score);

            if (!districtList.isEmpty()) {
                log.info("赋值中1");
                showInfoVo.setDistrictList(districtList);
                log.info("赋值中2");
                showInfoVo.setReviewed(String.valueOf(count));
                log.info("赋值中3");
                showInfoVo.setScore(String.valueOf(score));
                log.info("赋值中4");
                showInfoVo.setReviews(collect);
                log.info("赋值中5");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //释放锁
            unlock(LOCK_CODE_SHOWING_KEY);
        }
        //返回
        log.info("返回{}", showInfoVo);
        return showInfoVo;
    }

    /**
     * @Description: 景点数据渲染
     * @param: districtId, showInfoVo
     * @date: 2024/6/8 0:31
     */

    public ShowInfoVo dbFallBack(String districtId, ShowInfoVo showInfoVo, String scencyId) {
        //1查询地区名称--->地区表  查询评论数--->评论表  查询评分--->评论表
        //1.1缓存中查找地区是否存在
        String redisDistrictName = redisCache.getCacheObject(DISTRICT_CODE_KEY + scencyId);
        log.info("查询redis地区表(景点地区)成功！{}", redisDistrictName);
        //1.2缓存中查找对应景点或套餐的评论数
        Long num = redisCache.getCacheObject(REVIEW_NUM_KEY + scencyId);
        log.info("查询redis评论表(评论数)成功！{}", num);
        //1.3缓存中查找对应景点或套餐的评分
        Double redisScore = redisCache.getCacheObject(SCORE_NUM_KEY + scencyId);
        log.info("查询redis评论表(评分)成功！{}", redisScore);
        //1.4缓存中查找对应景点或套餐的评论
        List<ReviewVo> redisList = redisCache.getCacheList(REVIEW_CODE_KEY + scencyId);
        log.info("查询redis评论表(评论)成功！{}", redisList);
        //1.5判断地区是否为空
        if (StrUtil.isNotBlank(redisDistrictName)) {

            log.info("景点信息存在Redis中");
            //1.5不为空则一一赋值
            log.info("赋值中1");
            showInfoVo.setDistrict(redisDistrictName);
            log.info("赋值中2");
            showInfoVo.setReviewed(String.valueOf(num));
            log.info("赋值中3");
            showInfoVo.setScore(String.valueOf(redisScore));
            log.info("赋值中4");
            showInfoVo.setReviews(redisList);
            log.info("赋值中5");
            //返回
            return showInfoVo;
        }
        //2.为空则加锁去数据库查询
        try {
            boolean lock = tryLock(LOCK_CODE_SHOWING_KEY);
            //2.1判断加锁是否成功
            if (!lock) {
                //2.2拿锁失败就线程休眠1秒
                Thread.sleep(1000);
                return dbFallBack(districtId, showInfoVo, scencyId);
            }
            //2.3拿锁成功去查询数据库
            //2.3.1查询地区表(景点地区)并加入缓存

            log.info("景点信息不存在Redis中，拿到锁成功！");
            District one = districtService.lambdaQuery().eq(District::getId, districtId).one();
            String districtName = one.getName();
            redisCache.setCacheObject(DISTRICT_CODE_KEY + scencyId, districtName, DISTRICT_TTL_DAYS, TimeUnit.DAYS);
            log.info("查询地区表(景点地区)成功！{}", districtName);

            //2.3.2查询评论表(评论数)并加入缓存
            Long count = reviewService.lambdaQuery().eq(Review::getScencyId, scencyId).isNull(Review::getBelongId).count();
            redisCache.setCacheObject(REVIEW_NUM_KEY + scencyId, count, REVIEW_TTL_DAYS, TimeUnit.DAYS);
            log.info("查询评论表(评论数)成功！{}", count);

            //2.3.3查询评论表(评论)并加入缓存
            List<ReviewVo> collect = reviewService.lambdaQuery()
                    .eq(Review::getScencyId, scencyId)
                    .isNull(Review::getBelongId)
                    .list().stream().map(res ->
                    {
                        ReviewVo reviewVo = new ReviewVo();
                        reviewVo.setId(res.getId());
                        reviewVo.setContent(res.getContent());
                        reviewVo.setScore(String.valueOf(res.getScore()));
                        userService.lambdaQuery().eq(User::getId, res.getUserId()).list().forEach(var -> {
                            //2.3.3.1需要封装vo将用户的账户和头像封装
                            reviewVo.setName(var.getAccountId());
                            reviewVo.setAvatar(var.getAvatar());
                        });
                        //2.3.3.2封装子评论
                        List<RecoverVo> collect1 = reviewService.lambdaQuery()
                                .eq(Review::getScencyId, scencyId).isNull(Review::getScore)
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
            String listReviewKey = REVIEW_CODE_KEY + scencyId;
            redisCache.setCacheList(listReviewKey, collect);
            redisCache.expire(listReviewKey, REVIEW_TTL_DAYS, TimeUnit.DAYS);
            log.info("查询评论表(评论)成功！{}", collect);

            //2.3.4查询评论表(评分)并加入缓存
            double average = reviewService.getBaseMapper().selectObjs(new LambdaQueryWrapper<Review>()
                            .select(Review::getScore)
                            .eq(Review::getScencyId, scencyId)
                            .isNull(Review::getBelongId))
                    .stream().mapToDouble(res -> (float) res).average().orElse(0);
            double score = (Math.round(average * 10));
            score /= 10;
            redisCache.setCacheObject(SCORE_NUM_KEY + scencyId, score, REVIEW_TTL_DAYS, TimeUnit.DAYS);
            log.info("查询评论表(评分)成功！{}", score);

            if (StrUtil.isNotBlank(districtName)) {
                log.info("赋值中1");
                showInfoVo.setDistrict(districtName);
                log.info("赋值中2");
                showInfoVo.setReviewed(String.valueOf(count));
                log.info("赋值中3");
                showInfoVo.setScore(String.valueOf(score));
                log.info("赋值中4");
                showInfoVo.setReviews(collect);
                log.info("赋值中5");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //释放锁
            unlock(LOCK_CODE_SHOWING_KEY);
        }
        //返回
        log.info("返回{}", showInfoVo);
        return showInfoVo;
    }

    /**
     * @Description: 获取互斥锁
     * @param: key
     * @date: 2024/6/6 15:36
     */
    private boolean tryLock(String key) {
        boolean flag = redisCache.lock(key, "1", LOCK_CODE_TTL_SECONDS, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }

    /**
     * @Description: 释放互斥锁
     * @param: key
     * @date: 2024/6/6 15:36
     */
    private void unlock(String key) {

        redisCache.deleteObject(key);
    }

    /**
     * @Description: 景点%套餐是否点赞
     * @param: keyPrefix
     * id
     * @date: 2024/6/6 15:36
     */

    public <ID> Double isLike(String keyPrefix, ID id) {
        //1.首先拿到登录用户
        String userId = CommonHolder.getUser();
        log.info("拿到的用户：{}", userId);

        //2.判断是否登录
        if (StrUtil.isBlank(userId)) {
            return null;
        }
        //3.判断当前用户是否登录
        String key = keyPrefix + id;
        Double score = redisCache.score(key, userId);

        log.info("查询：{}", score);
        return score;
    }

    /**
     * @Description: 景点%套餐点赞
     * @param: keyPrefix1
     * id
     * type
     * keyPrefix2
     * @date: 2024/6/6 15:37
     */

    public <R> boolean like(String keyPrefix1, Long id, Class<R> type, String keyPrefix2) {

        //1.首先拿到登录用户
        String userId = CommonHolder.getUser();

        //2.判断是否点赞过
        //2.1拼接点赞的key
        String key1 = keyPrefix1 + id;
        //拼接景点信息key
        String key2 = keyPrefix2 + id;

        //2.2去redis中获取点赞缓存
        Double score = redisCache.score(key1, userId);

        //2.3构建条件构造器
        LambdaQueryWrapper<UserCollect> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //2.4判断是景点还是套餐
        boolean over = type == Scency.class;
        if (score == null) {
            //3.如果未点赞，则可以点赞
            //3.1数据库中点赞数加1--->update
            boolean update = over ?
                    scencyService.update().setSql("liked = liked + 1").eq("id", id).update()
                    : packageService.update().setSql("liked = liked + 1").eq("id", id).update();

            //3.2用户收藏表中加入个人收藏--->save
            UserCollect userCollect = new UserCollect();
            userCollect.setUserId(Long.valueOf(userId));
            if (over) {
                userCollect.setScencyId(id);
            } else {
                userCollect.setPackageId(id);
            }
            boolean save = userCollectService.save(userCollect);
            if (update && save) {
                //3.4点赞加1后将数据存到redis中
                redisCache.add(key1, userId, System.currentTimeMillis());
                //3.5更改存在redis中的景点信息点赞实现数据同步
                redisCache.incrementHash(key2, "liked", 1);
                return true;
            }
        } else {
            //4.如果点过赞了，就取消点赞
            //4.1数据库中点赞数减1--->update
            boolean update = over ?
                    scencyService.update().setSql("liked = liked - 1").eq("id", id).update()
                    : packageService.update().setSql("liked = liked - 1").eq("id", id).update();

            //4.22用户收藏表中删除个人收藏--->remove
            if (over) {
                lambdaQueryWrapper.eq(UserCollect::getScencyId, id);
            } else {
                lambdaQueryWrapper.eq(UserCollect::getPackageId, id);
            }
            boolean remove = userCollectService.remove(lambdaQueryWrapper);
            if (update && remove) {
                //4.2点赞减一后删除redis中的缓存
                redisCache.remove(key1, userId);
                //4.3更改存在redis中的景点信息点赞实现数据同步
                redisCache.incrementHash(key2, "liked", -1);
                return true;
            }
        }
        return false;
    }

    /**
     * @Description: 写入日志
     * * @date: 2024/5/30 19:59
     */

    public boolean record(String content) {

        //1.写入日志
        Records records = new Records();
        records.setContent(content);

        return recordsService.save(records);

    }

    /**
     * @Description: 热门景点%套餐
     * @param: keyPrefix
     * type
     * @date: 2024/6/6 15:38
     */

    public <R> PopularVo popular(String keyPrefix, Class<R> type) {

        //1.去缓存中寻找热门景点
        String json = redisCache.getCacheObject(keyPrefix);

        PopularVo popularVo = new PopularVo();

        //1.1判断类型
        boolean flag = type == Scency.class;
        //2.若缓存中没有热门景点
        if (StrUtil.isBlank(json)) {
            //2.1获取锁
            try {
                boolean isLock = tryLock(LOCK_CODE_POPULAR_KEY);

                //2.2判断是否获取锁
                if (!isLock) {
                    //2.3获取锁失败休眠1秒再次调用
                    log.info("没拿到锁，等待");
                    Thread.sleep(1000);
                    return popular(keyPrefix, type);
                }
                //2.3获取锁成功查询数据库并存到redis中
                log.info("拿到锁");
                return dbFallBack(flag, keyPrefix, popularVo);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                //释放锁
                unlock(LOCK_CODE_POPULAR_KEY);
            }
        }
        //3.若缓存中有热门景点
        RedisData redisData = JSONUtil.toBean(json, RedisData.class);
        LocalDateTime expireTime = redisData.getExpireTime();
        log.info("拿到的时间，{}", expireTime);
        Object data = redisData.getData();
        log.info("拿到的数据，{}", data);
        R r = JSONUtil.toBean((JSONObject) data, type);
        log.info("转化的数据{}", r);
        BeanUtil.copyProperties(data, popularVo, false);
        //3.1判断逻辑时间是否过期
        if (expireTime.isAfter(LocalDateTime.now())) {
            //3.2未过期就转化对象并返回
            log.info("数据没过期");
            log.info("返回的数据{}", popularVo);
            return popularVo;
        }
        //4.过期就重建缓存
        //4.1获取锁
        boolean isLock = tryLock(LOCK_CODE_POPULAR_KEY);
        //4.2判断获取锁是否成功
        if (isLock) {
            //4.3获取成功开启独立线程
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                try {
                    //4.4查询数据库并存到redis中
                    log.info("数据过期,拿到锁");
                    dbFallBack(flag, keyPrefix, popularVo);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                } finally {
                    //释放锁
                    unlock(LOCK_CODE_POPULAR_KEY);
                }
            });
        }
        //获取锁失败且缓存过期返回旧数据
        log.info("数据过期没拿到锁");
        return popularVo;
    }

    /**
     * @Description: 热门景点%套餐数据渲染
     * @param: flag
     * keyPrefix
     * popularVo
     * @date: 2024/6/7 14:21
     */

    public PopularVo dbFallBack(boolean flag, String keyPrefix, PopularVo popularVo) {
        if (flag) {
            Scency scency = scencyService.lambdaQuery().orderByDesc(Scency::getLiked).last("LIMIT 1").one();
            BeanUtil.copyProperties(scency, popularVo, false);
            setWithLogicalExpire(keyPrefix, scency, POPULAR_TTL_DAY, TimeUnit.DAYS);
        } else {
            Package aPackage = packageService.lambdaQuery().orderByDesc(Package::getLiked).last("LIMIT 1").one();
            BeanUtil.copyProperties(aPackage, popularVo, false);
            StringBuilder stringBuilder = new StringBuilder();
            packageDistrictService.lambdaQuery().eq(PackageDistrict::getPackageId, aPackage.getId())
                    .list().forEach(var -> {
                        List<String> list = districtService
                                .listObjs(new LambdaQueryWrapper<District>()
                                        .eq(District::getId, var.getDistrictId())
                                        .select(District::getName));
                        for (String jt : list) {
                            stringBuilder.append(jt).append("-");
                        }
                        String value = String.valueOf(stringBuilder);
                        String substring = value.substring(0, value.length() - 1);
                        popularVo.setAddress(substring);
                    });
            setWithLogicalExpire(keyPrefix, popularVo, POPULAR_TTL_DAY, TimeUnit.DAYS);
        }
        return popularVo;
    }

    /**
     * @Description: 查询地区
     * @param:
     * @date: 2024/6/7 14:19
     */

    public List<District> selectDisAll() {

        //1.从缓存中查找地区
        Map<String, District> map = redisCache.getCacheMap(DISTRICT_CODE_KEY);
        List<District> list = null;
        //1.1判断是否存在
        if (!map.isEmpty()) {
            //1.2存在就遍历集合返回需要的数据
            list = new ArrayList<>();
            for (Map.Entry<String, District> entry : map.entrySet()) {
                District district = entry.getValue();
                list.add(district);
            }
            log.info("拿到的集合为：{}", list);
            return list;
        }
        try {
            //2不存在就获取互斥锁
            boolean lock = tryLock(LOCK_CODE_DISTRICT_KEY);

            //2.1判断获取锁是否成功

            if (!lock) {
                //2.2获取锁失败就让线程等待
                log.info("获取锁失败");
                Thread.sleep(1000);
                return selectDisAll();
            }
            log.info("获取锁成功");
            //2.3获取锁成功去查询数据库
            list = dataBaseControl.selectDisAll();
            log.info("列表为,{}", list);
            //2.4同步数据到缓存中
            HashMap<String, District> hashMap = new HashMap<>();
            for (District district : list) {
                String id = district.getId().toString();
                log.info("拿到的id,{}", id);
                //2.5自定义map集合
                hashMap.put(id, district);
                String key = DISTRICT_CODE_KEY;
                log.info("集合为,{}", hashMap.size());
                //2.6存到缓存中并设置过期时间
                redisCache.setCacheMap(key, hashMap);
                redisCache.expire(key, DISTRICT_TTL_DAYS, TimeUnit.DAYS);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //释放锁
            unlock(LOCK_CODE_DISTRICT_KEY);
        }
        return list;
    }

    /**
     * @Description: 随机六个景点%套餐
     * @param:
     * @date: 2024/6/10 15:27
     */

    public <R> SelectRandomVo[][] selectRandom(String keyPrefix, Class<R> type) {
        //1.去缓存中寻找热门景点
        List<SelectRandomVo> redisList = redisCache.getCacheList(keyPrefix);
        //1.1判断类型
        boolean flag = type == Scency.class;

        SelectRandomVo[][] selectRandomVos = new SelectRandomVo[2][3];
        //1.2判断集合是否为空
        if (!redisList.isEmpty()) {

            SelectRandomVo[] randomVos = redisList.toArray(new SelectRandomVo[0]);
            int a = 0;
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 3; j++) {
                    selectRandomVos[i][j] = randomVos[a++];
                }
            }
            //1.2.1不为空则返回
            return selectRandomVos;
        }


        List<SelectRandomVo> list = null;
        try {
            //2.为空则加锁
            boolean lock = tryLock(LOCK_CODE_SELECTRANDOM_KEY);
            //2.1判断获取锁是否成功
            if (!lock) {
                //2.2获取锁失败休眠1秒再次调用
                log.info("没拿到锁，等待");
                Thread.sleep(1000);
                return selectRandom(keyPrefix, type);
            }
            //2.3获取锁成功查询数据库并存到redis中
            log.info("拿到锁");
            if (flag) {
                list = scencyService.lambdaQuery().orderByDesc(Scency::getUpdateTime)
                        .last("LIMIT 6").list()
                        .stream().map(res -> {
                            SelectRandomVo selectRandomVo = new SelectRandomVo();
                            selectRandomVo.setId(res.getId().toString());
                            selectRandomVo.setName(res.getName());
                            selectRandomVo.setImage(res.getImage());

                            double average = reviewService.listObjs(new LambdaQueryWrapper<Review>()
                                            .eq(Review::getScencyId, res.getId())
                                            .isNull(Review::getBelongId)
                                            .select(Review::getScore))
                                    .stream().mapToDouble(var -> (float) var).average().orElse(0);

                            double score = (Math.round(average * 10));
                            score /= 10;
                            selectRandomVo.setScore(score);
                            return selectRandomVo;
                        }).collect(Collectors.toList());
            } else {
                list = scencyService.lambdaQuery().orderByDesc(Scency::getUpdateTime)
                        .last("LIMIT 6").list()
                        .stream().map(res -> {
                            SelectRandomVo selectRandomVo = new SelectRandomVo();
                            selectRandomVo.setId(res.getId().toString());
                            selectRandomVo.setName(res.getName());
                            selectRandomVo.setImage(res.getImage());

                            double average = reviewService.listObjs(new LambdaQueryWrapper<Review>()
                                            .eq(Review::getScencyId, res.getId())
                                            .isNull(Review::getBelongId)
                                            .select(Review::getScore))
                                    .stream().mapToDouble(var -> (float) var).average().orElse(0);

                            double score = (Math.round(average * 10));
                            score /= 10;
                            selectRandomVo.setScore(score);
                            return selectRandomVo;
                        }).collect(Collectors.toList());
            }
            SelectRandomVo[] array = list.toArray(new SelectRandomVo[0]);
            selectRandomVos = new SelectRandomVo[2][3];
            int a = 0;
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 3; j++) {
                    selectRandomVos[i][j] = array[a++];
                }
            }
            redisCache.setCacheList(keyPrefix, list);
            redisCache.expire(keyPrefix, SELECTRANDOM_TTL_DAY, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //释放锁
            unlock(LOCK_CODE_SELECTRANDOM_KEY);
        }
        return selectRandomVos;
    }
}
