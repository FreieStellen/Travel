package com.travel.interceptor;

import com.travel.utils.CommonHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 *@ClassName IsLoginInterceptor 自定义拦截器（2） 判断前一个拦截器是否在CommonHolder中存了对象
 *@Author Freie  stellen
 *@Date 2024/5/8 17:45
 */
public class IsLoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //是否需要拦截（判断CommonHolder中有无用户）
        if (CommonHolder.getUser() == null) {

            //401状态码代表身份未经验证
            response.setStatus(401);
            return false;
        }
        return true;
    }
}
