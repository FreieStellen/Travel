package com.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.ResponseResult;
import com.travel.entity.Package;
import com.travel.entity.PackageScency;
import com.travel.entity.dto.PackageDto;
import com.travel.entity.vo.PopularVo;
import com.travel.entity.vo.SelectRandomVo;
import com.travel.entity.vo.ShowInfoVo;
import com.travel.mapper.PackageMapper;
import com.travel.service.PackageScencyService;
import com.travel.service.PackageService;
import com.travel.service.UserCollectService;
import com.travel.utils.CacheClient;
import com.travel.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.travel.utils.RedisConstants.*;

/*
 *@ClassName PackageServiceImpl
 *@Author Freie  stellen
 *@Date 2024/5/21 17:04
 */
@Slf4j
@Service
public class PackageServiceImpl extends ServiceImpl<PackageMapper, Package> implements PackageService {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private PackageScencyService packageScencyService;

    @Autowired
    private CacheClient cacheClient;

    @Autowired
    private UserCollectService userCollectService;

    /**
     * @Description: 添加套餐
     * @param: apackage
     * @date: 2024/5/21 17:11
     */

    @Transactional
    @Override
    public ResponseResult<String> add(PackageDto packageDto) {


        log.info(packageDto.toString());

        //1.添加套餐
        boolean save = save(packageDto);

        //1.1.判断是否添加成功
        if (!save) {
            return ResponseResult.error("添加套餐失败！");
        }

        Long id = packageDto.getId();
        //2.添加操作日志写入
        log.info("添加了" + packageDto.getName() + "(" + id + ")");

        //2.1添加日志内容
        boolean record = cacheClient.record("添加了" + packageDto.getName() + "(" + id + ")");

        if (!record) {
            throw new RuntimeException("添加日志表失败！");
        }
        //3.将套餐存入缓存中
        //3.1拼接key
        String key = PACKAGE_CODE_KEY + id;

        //3.2存入redis中
        redisCache.setCacheObject(key, packageDto, PACKAGE_CODE_TTL_MINUTES, TimeUnit.MINUTES);

        //4.拿到套餐景点集合
        List<PackageScency> packageScencies = packageDto.getPackageScencies();

        //4.1使用流的方式将id赋值
        List<PackageScency> list = packageScencies.stream().peek((item) ->
                item.setPackageId(id)).collect(Collectors.toList());

        log.info("套餐关联表：{}", list);
        //5.添加套餐——景点表
        boolean batch = packageScencyService.saveBatch(list);

        if (!batch) {
            return ResponseResult.error("添加套餐景点失败！");
        }

        return ResponseResult.success("添加成功！");
    }

    /**
     * @Description: 查询套餐
     * @param: id
     * @date: 2024/5/23 10:52
     */

    @Override
    public ResponseResult<ShowInfoVo> selectPackageById(Long id) {

        //查询套餐
        ShowInfoVo showInfoVo = cacheClient
                .queryWithPassThrough(PACKAGE_CODE_KEY, id, Package.class, this::getById, PACKAGE_CODE_TTL_MINUTES, TimeUnit.MINUTES);

        if (Objects.isNull(showInfoVo)) {
            return ResponseResult.error("获取失败！");
        }

        //判断是否点赞
        isLike(showInfoVo);
        return ResponseResult.success(showInfoVo);
    }

    /**
     * @Description: 是否点赞
     * @param: scency
     * @date: 2024/5/23 14:22
     */

    public void isLike(ShowInfoVo showInfoVo) {

        Double like = cacheClient.isLike(PACKAGE_LIKED_KEY, showInfoVo.getId());
        showInfoVo.setLike(like != null);

        log.info("拿到套餐：{}", showInfoVo);
    }

    /**
     * @Description: 点赞
     * @param: id
     * @date: 2024/5/23 11:02
     */

    @Transactional
    @Override
    public ResponseResult<String> likePackage(Long id) {

        boolean like = cacheClient.like(PACKAGE_LIKED_KEY, id, Package.class, PACKAGE_CODE_KEY);

        if (like) {
            return ResponseResult.success("点赞收藏套餐操作成功！");
        }
        return ResponseResult.error("点赞收藏套餐操作失败！");
    }

    /**
     * @Description: 套餐分页查询
     * @param: page, pageSize
     * @date: 2024/5/23 15:25
     */

    @Override
    public ResponseResult<Page<Package>> pagePackage(int current, int pageSize) {

        //1.构造分页构造器
        Page<Package> page = new Page<>(current, pageSize);

        //2.构造条件构造器
        LambdaQueryWrapper<Package> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Package::getStatus, 0).orderByDesc(Package::getCreateTime);

        //3.分页查询
        Page<Package> page1 = page(page, wrapper);

        return ResponseResult.success(page1);
    }

    @Override
    public ResponseResult<PopularVo> popular() {
        PopularVo popular = cacheClient.popular(PACKAGE_POPULAR_KEY, Package.class);

        if (Objects.isNull(popular)) {

            return null;
        }
        return ResponseResult.success(popular);
    }

    @Override
    public ResponseResult<List<SelectRandomVo>> selectRandom() {
        List<SelectRandomVo> list = cacheClient.selectRandom(PACKAGE_SELECTRANDOM_KEY, Package.class);

        if (list.isEmpty()) {
            return null;
        }
        return ResponseResult.success(list);
    }
}
