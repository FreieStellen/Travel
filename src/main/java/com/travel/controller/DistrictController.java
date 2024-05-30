package com.travel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.ResponseResult;
import com.travel.entity.District;
import com.travel.service.DistrictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
 *@ClassName CategoryController 分类控制层
 *@Author Freie  stellen
 *@Date 2024/5/20 16:38
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class DistrictController {

    @Autowired
    private DistrictService districtService;

    /**
     * @Description: 添加分类
     * @param: category
     * @date: 2024/5/20 17:21
     */

    @PostMapping("/save")
    public ResponseResult<String> add(@RequestBody District district) {

        log.info("添加的分类：{}", district);
        return districtService.add(district);
    }

    /**
     * @Description: 分页查询
     * @param:
     * @date: 2024/5/20 17:21
     */

    @GetMapping("/list")
    public ResponseResult<Page<District>> selectList() {
        return districtService.selectList();
    }
}
