package com.travel.config;

import com.travel.interceptor.CommonLoginInterceptor;
import com.travel.interceptor.IsLoginInterceptor;
import com.travel.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
 *@ClassName MvcConfig mvc配置类
 *@Author Freie  stellen
 *@Date 2024/5/8 17:55
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private RedisCache redisCache;

    /**
     * @Description: CommonLoginInterceptor拦截器是第一层拦截器，IsLoginInterceptor是第二层拦截器
     * @param: registry
     * @date: 2024/5/8 18:30
     */

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new IsLoginInterceptor())
                .excludePathPatterns(
                        "/common/sendmsg",
                        "/user/**",
                        "/manager/**",
                        "/common/**"
                ).order(1);//拦截器拦截顺序，默认的拦截顺序是都是0
        registry.addInterceptor(new CommonLoginInterceptor(redisCache)).order(0);
    }
}
