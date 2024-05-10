package com.travel.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.ResponseResult;
import com.travel.entity.Manager;
import com.travel.entity.vo.LoginByIdVo;
import com.travel.entity.vo.LoginByPhoneVo;
import com.travel.mapper.ManagerMapper;
import com.travel.service.ManagerService;
import com.travel.utils.JwtUtil;
import com.travel.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.travel.utils.RedisConstants.LOGIN_CODE_KEY;
import static com.travel.utils.RedisConstants.LOGIN_CODE_TTL_MINUTES;

/*
 *@ClassName ManagerServiceImpl 管理员的功能实现类
 *@Author Freie  stellen
 *@Date 2024/3/27 11:34
 */
@Slf4j
@Service
public class ManagerServiceImpl extends ServiceImpl<ManagerMapper, Manager> implements ManagerService {

    @Autowired
    private RedisCache redisCache;

    /**
     * @Description: 管理员登录
     * @param: manager session
     * @date: 2024/3/31 20:16
     */

    @Override
    public ResponseResult<HashMap<String, String>> loginNumber(LoginByIdVo loginByIdVo) {

        //将管理员密码进行MD5加密
        String password = loginByIdVo.getPassword();
        String hex = DigestUtils.md5DigestAsHex(password.getBytes());

        //Lambda表达式查询
        LambdaQueryWrapper<Manager> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //根据账号(唯一)进行查询
        lambdaQueryWrapper.eq(Manager::getMAccount, loginByIdVo.getUsername());

        //查询管理员状态
        lambdaQueryWrapper.eq(Manager::getMStatus, 1);

        Manager one = this.getOne(lambdaQueryWrapper);

        //判断查询结果是否为空
        if (Objects.isNull(one)) {
            return ResponseResult.error("该管理员不存在！");
        }

        //判断密码是否匹配
        if (!one.getMPassword().equals(hex)) {
            return ResponseResult.error("密码输入错误！");
        }

        //将对象转化为map在存到redis中
        Map<String, Object> map = BeanUtil.beanToMap(one);
        log.info(one.toString());

        //取出管理员id
        String id = one.getMId().toString();

        //生成JWT
        String jwt = JwtUtil.createJWT(id);

        String key = LOGIN_CODE_KEY + id;
        //将查询结果存在redis中
        redisCache.setCacheMap(key, map);
        redisCache.expire(key, LOGIN_CODE_TTL_MINUTES, TimeUnit.MINUTES);

        //将jwt返回给前端
        HashMap<String, String> hashMap = new HashMap<>();

        return ResponseResult.success(hashMap);
    }

    /**
     * @Description: 手机号登录
     * @param: manager session
     * @date: 2024/3/31 21:33
     */
    @Override
    public ResponseResult<Manager> loginPhone(LoginByPhoneVo manager) {


        return ResponseResult.error("登陆失败");
    }
}
