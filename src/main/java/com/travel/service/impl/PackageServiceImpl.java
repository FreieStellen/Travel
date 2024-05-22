package com.travel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.ResponseResult;
import com.travel.entity.Package;
import com.travel.entity.PackageScency;
import com.travel.entity.dto.PackageDto;
import com.travel.mapper.PackageMapper;
import com.travel.service.PackageScencyService;
import com.travel.service.PackageService;
import com.travel.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.travel.utils.RedisConstants.PACKAGE_CODE_KEY;
import static com.travel.utils.RedisConstants.PACKAGE_CODE_TTL_MINUTES;

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

        //2.判断是否添加成功
        if (!save) {
            return ResponseResult.error("添加套餐失败！");
        }
        //3.将套餐存入缓存中
        //3.1拼接key
        Long id = packageDto.getId();
        String key = PACKAGE_CODE_KEY + id;

        //3.2存入redis中
        redisCache.setCacheObject(key, packageDto, PACKAGE_CODE_TTL_MINUTES, TimeUnit.MINUTES);

        //4.拿到套餐景点集合
        List<PackageScency> packageScencies = packageDto.getPackageScencies();

        //4.1使用流的方式将id赋值
        List<PackageScency> list = packageScencies.stream().peek((item) ->
                item.setPackageId(id)).collect(Collectors.toList());

        log.info("套餐关联表：{}", list);
        //5.添加套餐——景点
        boolean batch = packageScencyService.saveBatch(list);

        if (!batch) {
            return ResponseResult.error("添加套餐景点失败！");
        }

        return ResponseResult.success("添加成功！");
    }
}
