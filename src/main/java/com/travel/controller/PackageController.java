package com.travel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.ResponseResult;
import com.travel.entity.Package;
import com.travel.entity.dto.PackageDto;
import com.travel.entity.vo.PopularVo;
import com.travel.entity.vo.SelectRandomVo;
import com.travel.entity.vo.ShowInfoVo;
import com.travel.service.PackageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseResult<ShowInfoVo> selectPackageById(@PathVariable Long id) {

        log.info("套餐id为：{}", id);
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

    /**
     * @Description: 分页查询
     * @param: page, pageSize
     * @date: 2024/5/23 15:36
     */

    @GetMapping("/page")
    public ResponseResult<Page<Package>> pagePackage(int current, int pageSize) {
        return packageService.pagePackage(current, pageSize);
    }

    @GetMapping("/popular")
    public ResponseResult<PopularVo> popular() {
        return packageService.popular();
    }

    /**
     * @Description: 随机查询六个套餐
     * @param:
     * @date: 2024/6/10 20:02
     */

    @GetMapping("/selectrandom")
    public ResponseResult<List<SelectRandomVo>> selectRandom() {
        return packageService.selectRandom();
    }
}
