package com.travel.controller;

import com.travel.common.ResponseResult;
import com.travel.entity.User;
import com.travel.entity.dto.LoginByIdDto;
import com.travel.entity.dto.LoginByPhoneDto;
import com.travel.entity.dto.UserRegistDto;
import com.travel.entity.vo.LoginUserVo;
import com.travel.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public ResponseResult<LoginUserVo> loginByUserName(@RequestBody LoginByIdDto user) {

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
    public ResponseResult<LoginUserVo> loginByPhone(@RequestBody LoginByPhoneDto user) {

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
    public ResponseResult<String> regist(@RequestBody UserRegistDto user) {
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
     * @Description: 回显登录, 记住密码
     * @param: username
     * @date: 2024/5/22 16:48
     */

    @GetMapping("/echo/{username}")
    public ResponseResult<String> echoLogin(@PathVariable String username) {
        log.info("回显登录：{}", username);
        return userService.echoLogin(username);

    }

    /**
     * @Description: 查询个人
     * @param: id, username
     * @date: 2024/6/3 23:13
     */

    @GetMapping("/selectable/{id}/{username}")
    public ResponseResult<User> SelectById(@PathVariable Long id, @PathVariable String username) {
        log.info("id：{},账号：{}", id, username);
        return userService.SelectById(id, username);

    }
}
