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
import com.travel.entity.Records;
import com.travel.entity.Scency;
import com.travel.entity.UserCollect;
import com.travel.entity.vo.PopularVo;
import com.travel.service.PackageService;
import com.travel.service.RecordsService;
import com.travel.service.ScencyService;
import com.travel.service.UserCollectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

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

    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);


    //设置逻辑过期以redisData封装类的形式存入redis（热门景点）
    public void setWithLogicalExpire(String key, Object value, Long time, TimeUnit unit) {
        // 设置逻辑过期
        RedisData redisData = new RedisData();
        redisData.setData(value);

        //设置逻辑过期时间
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));
        // 写入Redis
        redisCache.setCacheObject(key, JSONUtil.toJsonStr(redisData));
    }

    //解决缓存穿透和缓存击穿问题（使用互斥锁）
    public <R, ID> R queryWithPassThrough(
            String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit) {

        //拼接key
        String key = keyPrefix + id;

        //首先去redis中查找缓存景点
        Map<String, Object> map = redisCache.getCacheMap(key);

        //判断是否存在
        if (!map.isEmpty()) {

            if (map.size() == 1) {
                log.info("查询");
                return null;
            }
            //存在就将map对象转为scency对象返回(isToCamelCase表示是否将 Map 中的下划线命名转换为驼峰命名
            //CopyOptions.create()表示选择性拷贝属性)
            R r = BeanUtil.mapToBean(map, type, false, CopyOptions.create());

            log.info("查询到:{}", r);
            //返回景点信息
            return r;
        }
        //拼接锁的key
        String lockKey = LOCK_CODE_KEY + id;
        R r = null;
        try {
            //不存在去尝试获取锁
            boolean lock = tryLock(lockKey);

            //判断是否获取锁
            if (!lock) {
                //没有获取就让线程等待
                Thread.sleep(50);
                //递归调用
                return queryWithPassThrough(keyPrefix, id, type, dbFallback, time, unit);
            }
            //获取成功后去查询数据库
            //apply方法可以接受一个泛型ID对象返回一个泛型R对象
            r = dbFallback.apply(id);

            //判断拿到的对象是否为null
            if (Objects.isNull(r)) {

                Map<String, String> hashMap = new HashMap<>();
                hashMap.put("null", "");
                //为null就在redis中储存空值并设置过期时间解决缓存穿透问题
                redisCache.setCacheMap(key, hashMap);
                redisCache.expire(key, NULL_CODE_TTL_MINUTES, TimeUnit.MINUTES);
                //返回不存在
                return null;
            }
            //不为null就返回并且同步数据到缓存中
            Map<String, Object> map1 = BeanUtil.beanToMap(r);

            redisCache.setCacheMap(key, map1);
            redisCache.expire(key, time, unit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //释放锁
            unlock(lockKey);
        }
        return r;
    }

//    //解决缓存击穿问题（设置逻辑过期时间）
//    public <R, ID> R queryWithLogicalExpire(
//            String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit) {
//
//        //拼接key
//        String key = keyPrefix + id;
//
//        //首先去redis中查找缓存景点
//        String json = redisCache.getCacheObject(key);
//
//        //判断是否存在
//        if (json.isEmpty()) {
//
//            //不存在直接返回null（代表景点不是热点信息没存到redis中）
//            return null;
//        }
//        //命中就将对象反序列化为redisData
//        RedisData redisData = JSONUtil.toBean(json, RedisData.class);
//
//        //再将redisData中的data反序列化为需要的类型
//        R r = JSONUtil.toBean((JSONObject) redisData.getData(), type);
//        log.info("查询到:{}", r);
//
//        //拿到逻辑过期时间
//        LocalDateTime expireTime = redisData.getExpireTime();
//
//        //判断是否过期
//        if (expireTime.isAfter(LocalDateTime.now())) {
//
//            //未过期的话直接返回
//            return r;
//        }
//        //过期需要重新重置逻辑过期时间
//        //拼接互斥锁key
//        String lockKey = LOCK_CODE_KEY + id;
//
//        //首先获得互斥锁
//        boolean lock = tryLock(lockKey);
//
//        //判断是否获取互斥锁
//        if (lock) {
//            //拿到互斥锁开始重置逻辑过期时间
//            //开启独立线程开始缓存重建
//            CACHE_REBUILD_EXECUTOR.submit(() -> {
//
//                //查询数据库
//                R apply = dbFallback.apply(id);
//
//                //建立缓存重建
//                setWithLogicalExpire(key, apply, RedisConstants.SCENCY_CODE_TTL_MINUTES, TimeUnit.MINUTES);
//
//                //归还锁
//                unlock(lockKey);
//            });
//        }
//
//        return queryWithLogicalExpire(keyPrefix, id, type, dbFallback, time, unit);
//    }

    //获取互斥锁
    private boolean tryLock(String key) {
        boolean flag = redisCache.lock(key, "1", LOCK_CODE_TTL_SECONDS, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }

    //释放互斥锁
    private void unlock(String key) {

        redisCache.deleteObject(key);
    }

    //是否点赞
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

    //点赞
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
                redisCache.increment(key2, "liked", 1);
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
                redisCache.increment(key2, "liked", -1);
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

    //热门景点/套餐
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
        BeanUtil.copyProperties(r, popularVo, false);
        //3.1判断逻辑时间是否过期
        if (expireTime.isAfter(LocalDateTime.now())) {
            //3.2未过期就转化对象并返回
            log.info("数据没过期");
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

    //查询数据库并同步到redis中
    public PopularVo dbFallBack(boolean flag, String keyPrefix, PopularVo popularVo) {
        if (flag) {
            Scency scency = scencyService.lambdaQuery().orderByDesc(Scency::getLiked).last("LIMIT 1").one();
            BeanUtil.copyProperties(scency, popularVo, false);
            setWithLogicalExpire(keyPrefix, scency, POPULAR_TTL_DAY, TimeUnit.DAYS);
        } else {
            Package aPackage = packageService.lambdaQuery().orderByDesc(Package::getLiked).last("LIMIT 1").one();
            BeanUtil.copyProperties(aPackage, popularVo, false);
            setWithLogicalExpire(keyPrefix, aPackage, POPULAR_TTL_DAY, TimeUnit.DAYS);
        }
        return popularVo;
    }
}
