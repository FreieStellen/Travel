package com.travel.controller;

import com.travel.common.ResponseResult;
import com.travel.entity.dto.PackageDto;
import com.travel.service.PackageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
