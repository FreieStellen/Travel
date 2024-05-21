package com.travel.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.ResponseResult;
import com.travel.entity.User;
import com.travel.entity.vo.LoginByIdVo;
import com.travel.entity.vo.LoginByPhoneVo;
import com.travel.entity.vo.UserRegistVo;
import com.travel.mapper.UserMapper;
import com.travel.service.UserService;
import com.travel.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.travel.utils.RedisConstants.LOGIN_CODE_KEY;
import static com.travel.utils.RedisConstants.LOGIN_CODE_TTL_MINUTES;

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
        String code = userRegistVo.getCode();

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
        if (!RegexUtil.isCodeInvalid(code)) {

            //如果无效就返回
            return ResponseResult.error("验证码格式错误！");
        }

        //如果有效就去redis中拿到验证码
        String redisCode = redisCache.getCacheObject(phone);

        //判断redis中是否存在验证码
        if (StrUtil.isBlank(redisCode)) {
            //不存在则返回错误信息
            return ResponseResult.error("验证码失效，请重新发送短信");
        }

        //判断验证码是否一致
        if (!ValidateCodeUtils.matches(code, redisCode)) {
            return ResponseResult.error("验证码错误，请重新输入！");
        }

        User user = new User();
        //将userRegistVo和user相同的属性赋值给new User
        BeanUtil.copyProperties(userRegistVo, user);

        //拿到用户的密码进行加密处理
        String password = user.getuPassword();

        //将密码进行加密处理
        String encode = PasswordEncoder.encode(password);
        user.setUPassword(encode);

        boolean save = this.save(user);

        if (!save) {
            return ResponseResult.error("注册失败!");
        }
        return ResponseResult.success(user, "注册成功！");
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

    /**
     * @Description: 账号密码登录
     * @param: loginByIdVo
     * @date: 2024/5/13 17:49
     */

    @Override
    public ResponseResult<HashMap<String, Object>> loginByUserName(LoginByIdVo loginByIdVo) {


        //拿到用户密码
        String password = loginByIdVo.getPassword();

        //Lambda表达式查询
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //根据账号(唯一)进行查询
        lambdaQueryWrapper.eq(User::getuAccountId, loginByIdVo.getUsername());

        //查询管理员状态
        lambdaQueryWrapper.eq(User::getuStatus, 1);

        //得到查询结果
        User one = this.getOne(lambdaQueryWrapper);

        //判断查询结果是否为空
        if (Objects.isNull(one)) {
            return ResponseResult.error("该用户不存在！");
        }

        //判断密码是否匹配
        if (!PasswordEncoder.matches(one.getuPassword(), password)) {
            log.info("密码对比：{}", PasswordEncoder.matches(one.getuPassword(), password));
            return ResponseResult.error("输入的密码错误，请重新输入！");
        }

        //将对象转化为map在存到redis中
        Map<String, Object> map = BeanUtil.beanToMap(one);
        log.info(one.toString());

        //取出用户id
        String id = one.getuId().toString();

        //生成JWT
        String jwt = JwtUtil.createJWT(id);

        String key = LOGIN_CODE_KEY + id;
        //将查询结果存在redis中
        redisCache.setCacheMap(key, map);

        //设置用户登录时常为30分钟
        redisCache.expire(key, LOGIN_CODE_TTL_MINUTES, TimeUnit.MINUTES);

        //将jwt返回给前端
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("token", jwt);
        return ResponseResult.success(hashMap);
    }

    /**
     * @Description: 手机号验证码登录
     * @param: loginByPhoneVo
     * @date: 2024/5/13 17:37
     */

    @Override
    public ResponseResult<HashMap<String, Object>> loginByPhone(LoginByPhoneVo loginByPhoneVo) {

        //获取电话号码
        String phone = loginByPhoneVo.getPhone();

        //获取验证码
        String code = loginByPhoneVo.getCode();

        //判断电话号码是否为空
        if (StrUtil.isBlank(phone)) {

            //号码为空则返回
            return ResponseResult.error("电话不能为空！");
        }
        log.info("{}", RegexUtil.isPhoneInvalid(phone));

        //校验手机号是否有效
        if (!RegexUtil.isPhoneInvalid(phone)) {

            //如果无效就返回
            return ResponseResult.error("电话号码格式错误！");
        }
        //手机号无误后根据手机号去数据库查询
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        //查询手机号是否存在
        queryWrapper.eq(User::getuPhone, phone);
        //查询用户状态是否存在
        queryWrapper.eq(User::getuStatus, 1);

        //判断返回结果是否存在
        User user = this.getOne(queryWrapper);

        //不存在则返回错误信息
        if (Objects.isNull(user)) {
            return ResponseResult.error("该手机号未注册，请登陆后注册");
        }

        //存在则去redis中获取验证码
        String redisCode = redisCache.getCacheObject(phone);

        //判断验证码是否相等
        log.info("用户输入验证码：{}", code + "redis中的验证码:" + redisCode);

        //判断redis中是否存在验证码
        if (StrUtil.isBlank(redisCode)) {
            //不存在则返回错误信息
            return ResponseResult.error("验证码失效，请重新发送短信");
        }


        if (!ValidateCodeUtils.matches(code, redisCode)) {
            return ResponseResult.error("验证码错误，请重新输入！");
        }
        //拿到用户的id，根据id生成jwt
        String uid = user.getuId().toString();

        //生成jwt令牌
        String jwt = JwtUtil.createJWT(uid);

        log.info("{}", jwt);

        //将bean对象转化为map对象
        Map<String, Object> toMap = BeanUtil.beanToMap(user);

        String key = LOGIN_CODE_KEY + uid;

        //将用户信息存到redis中,过期时间为30分钟
        redisCache.setCacheMap(key, toMap);
        //给用户信息设置在redis的过期时间
        redisCache.expire(key, LOGIN_CODE_TTL_MINUTES, TimeUnit.MINUTES);

        //以键值对的形式将token返回给前端
        HashMap<String, Object> map = new HashMap<>();
        map.put("token", jwt);

        //返回成功信息
        return ResponseResult.success(map, "登陆成功");
    }

}
