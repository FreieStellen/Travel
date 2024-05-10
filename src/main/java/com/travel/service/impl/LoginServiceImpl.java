package com.travel.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.travel.common.LoginDetails;
import com.travel.common.ResponseResult;
import com.travel.entity.User;
import com.travel.entity.vo.LoginByIdVo;
import com.travel.entity.vo.LoginByPhoneVo;
import com.travel.service.LoginService;
import com.travel.service.UserService;
import com.travel.utils.JwtUtil;
import com.travel.utils.RedisCache;
import com.travel.utils.RegexUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.travel.utils.RedisConstants.LOGIN_CODE_KEY;
import static com.travel.utils.RedisConstants.LOGIN_CODE_TTL_MINUTES;

/*
 *@ClassName LoginServiceImpl 用户登陆的实现类
 *@Author Freie  stellen
 *@Date 2024/3/24 18:05
 */
@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    //自动装配认证管理器
    @Autowired
    private AuthenticationManager authenticationManager;

    //自动装配Redis工具类
    @Autowired
    private RedisCache redisCache;

    @Autowired
    private UserService userService;


    /**
     * @Description: 用户名密码登录操作
     * @param: loginByIdVo
     * @date: 2024/4/5 17:46
     */

    @Override
    public ResponseResult<HashMap<String, Object>> loginByUserName(LoginByIdVo loginByIdVo) {


        //定义一个用户名密码身份认证器（两参是）
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(loginByIdVo.getUsername(), loginByIdVo.getPassword());

        log.info(authenticationToken.toString());
        //定义一个authentication将用户密码身份认证器存到authentication中，相当于一个身份证
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        //获取重要的身份信息并强转成LoginDetails包装类
        LoginDetails principal = (LoginDetails) authentication.getPrincipal();

        //得到用户的用户名
        String uId = principal.getUser().getuId().toString();

        log.info("得到的用户id为" + uId);
        log.info("拿到的权限：{}", principal.getAuthorities().toString());

        //生成JWT令牌
        String jwt = JwtUtil.createJWT(uId);

        log.info(jwt);

        //将用户信息存储到redis中以便在授权时直接去redis查找，减轻了数据库的压力
        redisCache.setCacheObject(LOGIN_CODE_KEY + uId, principal, LOGIN_CODE_TTL_MINUTES, TimeUnit.MINUTES);

        //将token返回给前端
        HashMap<String, Object> map = new HashMap<>();

        map.put("Token", jwt);

        return ResponseResult.success(map, "登录成功！");
    }

    /**
     * @Description: 用户退出登录成功
     * @param:
     * @date: 2024/4/5 17:46
     */

    @Override
    public ResponseResult<String> logout() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        LoginDetails principal = (LoginDetails) authentication.getPrincipal();

        Long uId = principal.getUser().getuId();

        redisCache.deleteObject(LOGIN_CODE_KEY + uId);

        return ResponseResult.success("退出成功!");

    }

    /**
     * @Description: 用户手机验证码登录操作
     * @param: loginByPhoneVo
     * @date: 2024/4/29 11:01
     */

    @Override
    public ResponseResult<HashMap<String, Object>> loginByPhone(LoginByPhoneVo loginByPhoneVo) {

        //判断电话号码是否为空
        if (Objects.isNull(loginByPhoneVo.getPhone())) {

            //号码为空则返回
            return ResponseResult.error("电话不能为空！");
        }
        log.info("{}", RegexUtil.isPhoneInvalid(loginByPhoneVo.getPhone()));

        //校验手机号是否有效
        if (!RegexUtil.isPhoneInvalid(loginByPhoneVo.getPhone())) {

            //如果无效就返回
            return ResponseResult.error("电话号码格式错误！");
        }
        //手机号无误后根据手机号去数据库查询
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        //查询手机号是否存在
        queryWrapper.eq(User::getuPhone, loginByPhoneVo.getPhone());
        //查询用户状态是否存在
        queryWrapper.eq(User::getuStatus, 1);

        //判断返回结果是否存在
        User one = userService.getOne(queryWrapper);

        //不存在则返回错误信息
        if (Objects.isNull(one)) {
            return ResponseResult.error("该手机号未注册，请登陆后注册");
        }

        //存在则去redis中获取验证码
        Integer code = redisCache.getCacheObject(loginByPhoneVo.getPhone());

        //判断redis中是否存在验证码
        if (Objects.isNull(code)) {
            //不存在则返回错误信息
            return ResponseResult.error("验证码失效，请重新发送短信");
        }

        //存在就拿到用户的id，根据id生成jwt
        String uid = one.getuId().toString();

        //生成jwt令牌
        String jwt = JwtUtil.createJWT(uid);

        log.info("{}", jwt);

        //将bean对象转化为map对象
        Map<String, Object> toMap = BeanUtil.beanToMap(one);

        String key = LOGIN_CODE_KEY + uid;

        //将用户信息存到redis中,过期时间为十分钟
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
