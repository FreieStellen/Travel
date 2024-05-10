package com.travel.interceptor;

import com.travel.common.LoginDetails;
import com.travel.utils.JwtUtil;
import com.travel.utils.RedisCache;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.travel.utils.RedisConstants.LOGIN_CODE_KEY;

/*
 *@ClassName JwtAuthenticationTokenFilter 解析token的自定义过滤器
 *@Author Freie  stellen
 *@Date 2024/3/29 15:14
 */
@Slf4j
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisCache redisCache;

    /**
     * @Description: 解析Token的自定义过滤方法
     * @param: request response filterChain
     * @date: 2024/3/29 15:16
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //拿到请求头里面的token
        String token = request.getHeader("token");

        //hasText方法是判断字符序列是否为null且字符长度大于0且不含有空白字符序列返回true
        if (!StringUtils.hasText(token)) {

            //放行时会被后续过滤器拦截
            filterChain.doFilter(request, response);
            return;
        }
        String user;
        try {
            //解析token
            Claims claims = JwtUtil.parseJWT(token);


            //得到JSON数据
            user = claims.getSubject();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("token非法");
        }

        //将我们所需要去redis查询的key抽离出来
        String key = LOGIN_CODE_KEY + user;

        //拿到key去redis中查到用户
        LoginDetails cacheObject = redisCache.getCacheObject(key);

        //判断拿到的对象是否为空
        if (Objects.isNull(cacheObject)) {
            throw new RuntimeException("用户未登录");
        }

        log.info("拿到的权限为" + cacheObject.getAuthorities());

        //将权限赋予身份证
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(cacheObject, null, cacheObject.getAuthorities());

        //将用户信息和权限存到authenticationToken
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        //刷新用户登陆的有效期
        redisCache.expire(key, 10, TimeUnit.MINUTES);

        filterChain.doFilter(request, response);
    }
}
