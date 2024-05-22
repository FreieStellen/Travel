package com.travel.controller;

import com.travel.common.ResponseResult;
import com.travel.entity.vo.LoginByIdVo;
import com.travel.entity.vo.LoginByPhoneVo;
import com.travel.entity.vo.UserRegistVo;
import com.travel.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/*
 *@ClassName UserLogin 用户控制层
 *@Author Freie  stellen
 *@Date 2024/3/24 22:13
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * @Description: 用户密码登录
     * @param: user
     * @date: 2024/4/3 10:12
     */

    @PostMapping("/loginbyusername")
    public ResponseResult<HashMap<String, Object>> loginByUserName(@RequestBody LoginByIdVo user) {

        log.info("{}", user.toString());
        //登录
        return userService.loginByUserName(user);
    }

    /**
     * @Description: 手机号验证码登录
     * @param: user
     * @date: 2024/5/13 15:35
     */

    @PostMapping("/loginbyphone")
    public ResponseResult<HashMap<String, Object>> loginByPhone(@RequestBody LoginByPhoneVo user) {

        log.info("{}", user.toString());
        //登录
        return userService.loginByPhone(user);
    }

    /**
     * @Description: 注册用户
     * @param: user code session
     * @date: 2024/3/31 20:13
     */

    @PostMapping("/regist")
    public ResponseResult<String> regist(@RequestBody UserRegistVo user) {
        log.info(user.getAccountId());
        return userService.regist(user);
    }


    /**
     * @Description: 用户名查重
     * @param: username
     * @date: 2024/3/31 20:15
     */

    @GetMapping("/verify/{username}")
    public ResponseResult<Object> verifyUserName(@PathVariable String username) {
        return userService.verifyUserName(username);
    }

    /**
     * @Description: 回显登录
     * @param: username
     * @date: 2024/5/22 16:48
     */

    @GetMapping("/echo/{username}")
    public ResponseResult<Map<String, String>> echoLogin(@PathVariable String username) {
        log.info("回显登录：{}", username);
        return userService.echoLogin(username);

    }
}
