package com.travel.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.ResponseResult;
import com.travel.entity.User;
import com.travel.entity.vo.UserRegistVo;
import com.travel.mapper.UserMapper;
import com.travel.service.UserService;
import com.travel.utils.RedisCache;
import com.travel.utils.RegexUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

/*
 *@ClassName UserServiceImpl 用户相关功能的实现类
 *@Author Freie  stellen
 *@Date 2024/3/24 22:17
 */
@Slf4j

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private RedisCache redisCache;

    /**
     * @Description: 注册用户
     * @param: user code session
     * @date: 2024/3/29 14:21
     */

    @Override
    public ResponseResult<User> regist(UserRegistVo userRegistVo) {


        String phone = userRegistVo.getuPhone();
        Integer code = userRegistVo.getCode();

        //校验手机号是否有效
        if (!RegexUtil.isPhoneInvalid(phone)) {

            //如果无效就返回
            return ResponseResult.error("电话号码格式错误！");
        }

        //校验身份证是否有效
        if (!RegexUtil.isNumberInvalid(userRegistVo.getuNumber())) {

            //如果无效就返回
            return ResponseResult.error("身份证格式错误！");
        }
        //校验验证码是否有效
        if (!RegexUtil.isCodeInvalid(code.toString())) {

            //如果无效就返回
            return ResponseResult.error("验证码格式错误！");
        }

        //如果有效就去redis中拿到验证码
        Integer redisCode = redisCache.getCacheObject(phone);

        if (!Objects.equals(redisCode, code)) {
            return ResponseResult.error("验证码已过期！");
        }

        User user = new User();
        //将userRegistVo和user相同的属性赋值给new User
        BeanUtil.copyProperties(userRegistVo, user);

        //拿到用户的密码进行加密处理
        String password = user.getuPassword();

        //将密码进行加密处理
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode(password);
        user.setUPassword(encode);

        boolean save = this.save(user);

        if (!save) {
            return ResponseResult.error("添加失败!");
        }
        return ResponseResult.success(user, "添加成功！");
    }


    /**
     * @Description: 用户名查重
     * @param: username
     * @date: 2024/3/29 14:21
     */

    @Override
    public ResponseResult<String> verifyUserName(String username) {

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        lambdaQueryWrapper.eq(User::getuAccountId, username);
        lambdaQueryWrapper.eq(User::getuStatus, 1);

        User one = this.getOne(lambdaQueryWrapper);

        if (!Objects.isNull(one)) {
            return ResponseResult.error("该用户已存在!");
        }


        return ResponseResult.success("该用户不存在,可以注册");
    }

}
