package com.travel.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 *@ClassName MybatisPlusConfig MybatisPlus的分页拦截器
 *@Author Freie  stellen
 *@Date 2024/3/24 22:05
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * @Description: MP拦截器配置
     * @param:
     * @date: 2024/3/24 22:07
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {

        //创建一个MP拦截器实例
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();

        //添加一个具体的分页拦截器
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));

        return mybatisPlusInterceptor;
    }
}
