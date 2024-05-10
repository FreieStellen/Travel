package com.travel.controller;

import com.travel.common.ResponseResult;
import com.travel.entity.User;
import com.travel.entity.vo.LoginByIdVo;
import com.travel.entity.vo.LoginByPhoneVo;
import com.travel.entity.vo.UserRegistVo;
import com.travel.service.LoginService;
import com.travel.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/*
 *@ClassName UserLogin 用户接口
 *@Author Freie  stellen
 *@Date 2024/3/24 22:13
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private LoginService loginService;


    /**
     * @Description: 用户登录方法
     * @param: user
     * @date: 2024/4/3 10:12
     */

    @PostMapping("/loginbyusername")
    public ResponseResult<HashMap<String, Object>> loginByUserName(@RequestBody LoginByIdVo user) {

        log.info("{}", user.toString());
        //登录
        return loginService.loginByUserName(user);
    }

    @PostMapping("/loginbyphone")
    public ResponseResult<HashMap<String, Object>> loginByPhone(@RequestBody LoginByPhoneVo user) {

        log.info("{}", user.toString());
        //登录
        return loginService.loginByPhone(user);
    }

    /**
     * @Description: 注册用户方法
     * @param: user code session
     * @date: 2024/3/31 20:13
     */

    @PostMapping("/regist")
    public ResponseResult<User> regist(@RequestBody UserRegistVo user) {
        log.info(user.getuAccountId());
        return userService.regist(user);
    }
    

    /**
     * @Description: 用户名查重
     * @param: username
     * @date: 2024/3/31 20:15
     */

    @GetMapping("/verify/{username}")
    public ResponseResult<String> verifyUserName(@PathVariable String username) {
        return userService.verifyUserName(username);
    }

    @GetMapping("/logout")
    public ResponseResult<String> logout() {
        return loginService.logout();
    }
}
