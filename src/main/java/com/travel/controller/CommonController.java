package com.travel.controller;

import cn.hutool.core.util.StrUtil;
import com.travel.common.CommonHolder;
import com.travel.common.ResponseResult;
import com.travel.utils.RedisCache;
import com.travel.utils.RegexUtil;
import com.travel.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

import static com.travel.utils.RedisConstants.LOGIN_CODE_KEY;
import static com.travel.utils.RedisConstants.PHONE_CODE_TTL_MINUTES;

/*
 *@ClassName CommonController 公共控制层
 *@Author Freie  stellen
 *@Date 2024/5/7 23:52
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Autowired
    private RedisCache redisCache;

    /**
     * @Description: 发送手机验证码(已测试)
     * @param: phone
     * @date: 2024/3/29 14:21
     */
    @GetMapping("/sendmsg/{phone}")
    public ResponseResult<String> sendMsg(@PathVariable String phone) {

        log.info("拿到的电话为：{}", phone);
        //判断电话号码是否为空(如果传入的字符串为null、空字符串(“”)或仅包含空白字符（如空格、制表符、换行符等），则返回true；否则返回false。)
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
        //如果有效就生成随机的验证码
        String code = ValidateCodeUtils.generateValidateCode();

        log.info("生成的验证码为:{}", code);

        //将手机号和验证码存到redis中并设置过期时间,时间为1分钟
        redisCache.setCacheObject(phone, code, PHONE_CODE_TTL_MINUTES, TimeUnit.MINUTES);

        //返回发送成功消息
        return ResponseResult.success(code, "随机验证码生成！");

    }

    /**
     * @Description: 退出登录账户功能
     * @param:
     * @date: 2024/5/13 18:01
     */

    @GetMapping("/logout")
    public ResponseResult<String> logout() {
        //获取当前登录的用户
        String id = CommonHolder.getUser();

        log.info("当前线程用户：{}", id);

        //拼接redis的key
        String key = LOGIN_CODE_KEY + id;

        //从redis中移除用户
        boolean deleteObject = redisCache.deleteObject(key);

        if (!deleteObject) {
            return ResponseResult.error("登陆状态异常！");
        }
        return ResponseResult.success("注销账户成功！");
    }

}
