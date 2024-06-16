package com.travel.controller;

import com.travel.common.ResponseResult;
import com.travel.entity.vo.UserCollectVo;
import com.travel.service.UserCollectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/*
 *@ClassName UserCollectController
 *@Author Freie  stellen
 *@Date 2024/6/16 21:10
 */
@Slf4j
@RestController
@RequestMapping("/usercollect")
public class UserCollectController {

    @Resource
    private UserCollectService userCollectService;

    @GetMapping("/collect")
    public ResponseResult<List<UserCollectVo>> selectCollect() {

        return userCollectService.selectCollect();
    }
}
