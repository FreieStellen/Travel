package com.travel.interceptor;

import cn.hutool.core.bean.BeanUtil;
import com.travel.entity.dto.CommonDto;
import com.travel.utils.CommonHolder;
import com.travel.utils.JwtUtil;
import com.travel.utils.RedisCache;
import io.jsonwebtoken.Claims;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.travel.utils.RedisConstants.LOGIN_CODE_KEY;
import static com.travel.utils.RedisConstants.LOGIN_CODE_TTL_MINUTES;

/*
 *@ClassName ManagerLoginInterceptor 管理员自定义拦截器(2)
 *@Author Freie  stellen
 *@Date 2024/5/8 9:58
 */
public class ManagerLoginInterceptor implements HandlerInterceptor {

    private final RedisCache redisCache;

    public ManagerLoginInterceptor(RedisCache redisCache) {
        this.redisCache = redisCache;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        //获取请求头中的token
        String token = request.getHeader("token");

        //当字符串满足这三种情况的时候会返回true：字符串不为空、不是空白字符、字符串长度大于0
        if (StringUtils.hasText(token)) {
            return true;
        }
        //解析token
        Claims claims = JwtUtil.parseJWT(token);

        //拿到用户的id
        String user = claims.getSubject();

        //拿到redis中的key
        String key = LOGIN_CODE_KEY + user;

        //拿到id去redis中获取用户信息
        Map<String, Object> map = redisCache.getCacheMap(key);

        //判断拿到的集合是否为空
        if (map.isEmpty()) {
            return true;
        }
        //刷新在redis中的登陆时间
        redisCache.expire(key, LOGIN_CODE_TTL_MINUTES, TimeUnit.MINUTES);

        //将用户信息转化为CommonDto
        CommonDto commonDto = BeanUtil.fillBeanWithMap(map, new CommonDto(), false);

        //将用户信息存到ThreadLocal中
        CommonHolder.saveUser(commonDto);
        //放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        CommonHolder.removeUser();
    }
}
