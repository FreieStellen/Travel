package com.travel.controller;

import com.travel.common.ResponseResult;
import com.travel.entity.Manager;
import com.travel.entity.vo.LoginByIdVo;
import com.travel.entity.vo.LoginByPhoneVo;
import com.travel.service.ManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/*
 *@ClassName ManagerController 管理员接口
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

    @PostMapping("/loginNumber")
    public ResponseResult<HashMap<String, String>> loginNumber(@RequestBody LoginByIdVo loginByIdVo) {

        log.info(loginByIdVo.getPassword());
        return managerService.loginNumber(loginByIdVo);
    }

    /**
     * @Description: 手机号的登录
     * @param: manager code
     * @date: 2024/3/31 21:08
     */

    @PostMapping("/loginPhone")
    public ResponseResult<Manager> loginPhone(@RequestBody LoginByPhoneVo manager) {

        return managerService.loginPhone(manager);
    }
}
