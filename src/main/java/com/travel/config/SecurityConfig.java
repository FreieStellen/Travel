package com.travel.config;

import com.travel.interceptor.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/*
 *@ClassName SecurityConfig SpringSecurity的配置
 *@Author Freie  stellen
 *@Date 2024/3/24 17:02
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)//启用基于注解的权限控制
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    /**
     * @Description: SpringSecurity内置密码加密方法（注册用户时，使用SHA-256+随机盐+密钥把用户输入的密码进行hash处理，得到密码的hash值，然后将其存入数据库中
     * 加密结果不可逆，当登陆时将用户输入的密码进行加密与数据库中的加密密码进行对比）
     * @param:
     * @date: 2024/3/24 17:10
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * @Description: SpringSecurity的配置
     * @param: http
     * @date: 2024/3/29 16:34
     */

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //关闭csrf
                .csrf().disable()

                //不通过Session获取SecurityContext
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // 对于以下接口 允许匿名访问，不通过security的过滤链

                .antMatchers("/user/login").anonymous()
                .antMatchers("/user/regist").anonymous()
                .antMatchers("/manager/**").permitAll()
                .antMatchers("/user/**").permitAll()
                .antMatchers("/common/**").permitAll()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated();

        //添加过滤器在UsernamePasswordAuthenticationFilter过滤器前
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
