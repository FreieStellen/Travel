package com.travel.controller;

import com.travel.common.ResponseResult;
import com.travel.entity.Scency;
import com.travel.service.ScencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
 *@ClassName ScencyController 景点控制层
 *@Author Freie  stellen
 *@Date 2024/5/13 19:10
 */
@Slf4j
@RestController
@RequestMapping("/scency")
public class ScencyController {

    @Autowired
    private ScencyService scencyService;

    /**
     * @Description: 添加景点
     * @param: scency
     * @date: 2024/5/13 19:15
     */

    @PostMapping("/add")
    public ResponseResult<String> add(@RequestBody Scency scency) {

        log.info("景点为：{}", scency);
        return scencyService.add(scency);
    }

    /**
     * @Description: 根据id查询景点
     * @param: scency
     * @date: 2024/5/13 20:45
     */

    @GetMapping("/select/{id}")
    public ResponseResult<Scency> selectScencyById(@PathVariable Long id) {

        log.info("景点id为：{}", id);
        return scencyService.selectScencyById(id);
    }

    /**
     * @Description: 点赞
     * @param: id
     * @date: 2024/5/20 10:28
     */

    @GetMapping("/like/{id}")
    public ResponseResult<String> likeScency(@PathVariable Long id) {
        log.info("景点id：{}", id);
        return scencyService.likeScency(id);
    }
}
