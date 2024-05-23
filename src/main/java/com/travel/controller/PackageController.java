package com.travel.controller;

import com.travel.common.ResponseResult;
import com.travel.entity.Package;
import com.travel.entity.dto.PackageDto;
import com.travel.service.PackageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
 *@ClassName PackageController
 *@Author Freie  stellen
 *@Date 2024/5/21 17:05
 */
@Slf4j
@RestController
@RequestMapping("/package")
public class PackageController {

    @Autowired
    private PackageService packageService;

    /**
     * @Description: 添加套餐
     * @param: apackage
     * @date: 2024/5/21 17:11
     */

    @PostMapping("/add")
    public ResponseResult<String> add(@RequestBody PackageDto packageDto) {

        log.info("套餐：{}", packageDto.toString());
        return packageService.add(packageDto);
    }

    /**
     * @Description: 查询套餐
     * @param: id
     * @date: 2024/5/23 10:49
     */

    @GetMapping("/select/{id}")
    public ResponseResult<Package> selectPackageById(@PathVariable Long id) {

        log.info("景点id为：{}", id);
        return packageService.selectPackageById(id);
    }

    /**
     * @Description: 点赞
     * @param: id
     * @date: 2024/5/23 11:01
     */

    @GetMapping("/like/{id}")
    public ResponseResult<String> likePackage(@PathVariable Long id) {
        log.info("景点id：{}", id);
        return packageService.likePackage(id);
    }
}
