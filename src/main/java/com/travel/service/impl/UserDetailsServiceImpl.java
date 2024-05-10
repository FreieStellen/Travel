package com.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.travel.common.LoginDetails;
import com.travel.entity.User;
import com.travel.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/*
 *@ClassName UserDetailsServiceImpl
 *@Author Freie  stellen
 *@Date 2024/3/23 21:30
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    /**
     * @Description: 根据用户名（唯一）查询到用户，以便后续认证和授权操作
     * @param: username
     * @date: 2024/3/23 21:59
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //根据用户名（唯一）查询用户
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getuAccountId, username);
        lambdaQueryWrapper.eq(User::getuStatus, 1);
        User user = userMapper.selectOne(lambdaQueryWrapper);

        //判断查询到的用户是否为null
        if (Objects.isNull(user)) {
            throw new RuntimeException("用户名或密码错误");
        }

        //构建权限列表将权限和对象一起封装到类里面
        List<String> list = new ArrayList<>(Arrays.asList("test", "manager"));
        return new LoginDetails(user, list);
    }


}
