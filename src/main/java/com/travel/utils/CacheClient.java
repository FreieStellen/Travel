package com.travel.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.travel.common.CommonHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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

//    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);


//    //设置逻辑过期以redisData封装类的形式存入redis
//    public void setWithLogicalExpire(String key, Object value, Long time, TimeUnit unit) {
//        // 设置逻辑过期
//        RedisData redisData = new RedisData();
//        redisData.setData(value);
//
//        //设置逻辑过期时间
//        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));
//        // 写入Redis
//        redisCache.setCacheObject(key, redisData);
//    }

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
}
