package com.travel.controller;

import com.travel.common.ResponseResult;
import com.travel.entity.Scency;
import com.travel.entity.vo.PopularVo;
import com.travel.entity.vo.SelectRandomVo;
import com.travel.entity.vo.ShowInfoVo;
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
    public ResponseResult<ShowInfoVo> selectScencyById(@PathVariable Long id) {

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

    /**
     * @Description: 热门景点
     * @date: 2024/6/3 15:59
     */

    @GetMapping("/popular")
    public ResponseResult<PopularVo> popular() {
        return scencyService.popular();
    }

    /**
     * @Description: 随机查询六个景点
     * @param:
     * @date: 2024/6/10 20:00
     */

    @GetMapping("/selectrandom")
    public ResponseResult<SelectRandomVo[][]> selectRandom() {
        return scencyService.selectRandom();
    }
}
