package com.travel.controller;

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

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.travel.utils.RedisConstants.PHONE_CODE_TTL_MINUTES;

/*
 *@ClassName CommonController 公共控制类
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
     * @Description: 发送手机验证码
     * @param: phone
     * @date: 2024/3/29 14:21
     */
    @GetMapping("/sendmsg/{phone}")
    public ResponseResult<Integer> sendMsg(@PathVariable String phone) {

        //判断电话号码是否为空
        if (Objects.isNull(phone)) {

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
        Integer code = ValidateCodeUtils.generateValidateCode();

        log.info("生成的验证码为:{}", code);

        //将手机号和验证码存到redis中并设置过期时间,时间为1分钟
        redisCache.setCacheObject(phone, code, PHONE_CODE_TTL_MINUTES, TimeUnit.MINUTES);

        //返回发送成功消息
        return ResponseResult.success(code, "随机验证码生成！");

    }

}
