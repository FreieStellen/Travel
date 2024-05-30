package com.travel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.ResponseResult;
import com.travel.entity.Manager;
import com.travel.entity.dto.LoginByIdDto;
import com.travel.entity.dto.LoginByPhoneDto;
import com.travel.service.ManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/*
 *@ClassName ManagerController 管理员控制层
 *@Author Freie  stellen
 *@Date 2024/3/27 11:32
 */
@Slf4j
@RestController
@RequestMapping("/manager")
public class ManagerController {
    @Autowired
    private ManagerService managerService;

    /**
     * @Description: 账号密码登录
     * @param: manager session
     * @date: 2024/3/31 20:16
     */

    @PostMapping("/loginbyusername")
    public ResponseResult<HashMap<String, String>> loginByUserName(@RequestBody LoginByIdDto loginByIdDto) {

        log.info(loginByIdDto.getPassword());
        return managerService.loginByUserName(loginByIdDto);
    }

    /**
     * @Description: 手机号验证码登录
     * @param: manager code
     * @date: 2024/3/31 21:08
     */

    @PostMapping("/loginbyphone")
    public ResponseResult<HashMap<String, Object>> loginByPhone(@RequestBody LoginByPhoneDto manager) {

        return managerService.loginByPhone(manager);
    }

    /**
     * @Description: 管理员分页排序
     * @param: null
     * @date: 2024/5/13 20:16
     */
    @GetMapping("/page")
    public ResponseResult<Page<Manager>> managerPage(int page, int pageSize) {

        log.info("当前页：{},一页多少条数据：{}", page, pageSize);
        return managerService.managerPage(page, pageSize);
    }
}
