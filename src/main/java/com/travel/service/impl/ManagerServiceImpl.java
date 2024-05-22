package com.travel.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.ResponseResult;
import com.travel.entity.Manager;
import com.travel.entity.vo.LoginByIdVo;
import com.travel.entity.vo.LoginByPhoneVo;
import com.travel.mapper.ManagerMapper;
import com.travel.service.ManagerService;
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
     * @Description: 管理员账号密码登录
     * @param: manager session
     * @date: 2024/3/31 20:16
     */

    @Override
    public ResponseResult<HashMap<String, String>> loginByUserName(LoginByIdVo loginByIdVo) {

        //拿到管理员密码
        String password = loginByIdVo.getPassword();

        //Lambda表达式查询
        LambdaQueryWrapper<Manager> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //根据账号(唯一)进行查询
        lambdaQueryWrapper.eq(Manager::getAccount, loginByIdVo.getUsername());

        //查询管理员状态
        lambdaQueryWrapper.eq(Manager::getStatus, 1);

        //得到查询结果
        Manager one = this.getOne(lambdaQueryWrapper);

        //判断查询结果是否为空
        if (Objects.isNull(one)) {
            return ResponseResult.error("该管理员不存在！");
        }

        //判断密码是否匹配
        if (!PasswordEncoder.matches(one.getPassword(), password)) {
            log.info("密码对比：{}", PasswordEncoder.matches(one.getPassword(), password));
            return ResponseResult.error("输入的密码错误，请重新输入！");
        }

        //将对象转化为map在存到redis中
        Map<String, Object> map = BeanUtil.beanToMap(one);
        log.info(one.toString());

        //取出管理员id
        String id = one.getId().toString();

        //生成JWT
        String jwt = JwtUtil.createJWT(id);

        String key = LOGIN_CODE_KEY + id;
        //将查询结果存在redis中
        redisCache.setCacheMap(key, map);

        //设置用户登录时常为30分钟
        redisCache.expire(key, LOGIN_CODE_TTL_MINUTES, TimeUnit.MINUTES);

        //将jwt返回给前端
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", jwt);
        return ResponseResult.success(hashMap);
    }

    /**
     * @Description: 手机号验证码登录
     * @param: manager session
     * @date: 2024/3/31 21:33
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
        LambdaQueryWrapper<Manager> queryWrapper = new LambdaQueryWrapper<>();

        //查询手机号是否存在
        queryWrapper.eq(Manager::getPhone, phone);
        //查询用户状态是否存在
        queryWrapper.eq(Manager::getStatus, 1);

        //判断返回结果是否存在
        Manager manager = this.getOne(queryWrapper);

        //不存在则返回错误信息
        if (Objects.isNull(manager)) {
            return ResponseResult.error("该手机号未注册，请登陆后注册");
        }

        //存在则去redis中获取验证码
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
        //存在就拿到用户的id，根据id生成jwt
        String uid = manager.getId().toString();

        //生成jwt令牌
        String jwt = JwtUtil.createJWT(uid);

        log.info("{}", jwt);

        //将bean对象转化为map对象
        Map<String, Object> toMap = BeanUtil.beanToMap(manager);

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

    /**
     * @Description: 管理员分页查询
     * @param: page，pageSize
     * @date: 2024/5/13 20:30
     */
    @Override
    public ResponseResult<Page<Manager>> managerPage(int page, int pageSize) {

        //构造分页构造器
        Page<Manager> Page = new Page<>(page, pageSize);

        //构造条件器
        LambdaQueryWrapper<Manager> queryWrapper = new LambdaQueryWrapper<>();

        //构造过滤条件(状态为1即存在)和排序条件(根据创建时间降序排序)
        queryWrapper.eq(Manager::getStatus, 1)
                .orderByDesc(Manager::getCreateTime);

        //执行语句
        Page<Manager> page1 = this.page(Page, queryWrapper);

        return ResponseResult.success(page1);
    }

}
