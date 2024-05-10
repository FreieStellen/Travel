package com.travel.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.travel.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/*
 *@ClassName UserDetails SpringSecurity在访问数据层时所返回的用户包装类
 *@Author Freie  stellen
 *@Date 2024/3/23 21:40
 */
@Data
@NoArgsConstructor
public class LoginDetails implements UserDetails {

    private User user;

    //用一个集合来存权限（这里的权限相当于几个字符串）
    private List<String> permissions;


    public LoginDetails(User user, List<String> permissions) {
        this.user = user;
        this.permissions = permissions;
    }

    /**
     * @Description: 授予用户的权限
     * @param:
     * @date: 2024/3/23 21:52
     */

    //此注解的作用时在redis进行存储时不序列化，因为redis在存储时鉴于安全考虑不会序列化这个类
    @JSONField(serialize = false)
    //定义一个由权限字符串封装成SimpleGrantedAuthority类型对象的集合（将权限字符串转化为SpringSecurity认识的类）
    private List<SimpleGrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        //判断集合是否为空，为空则还没转化，不为空则转化。避免了每次都需要去转化
        if (authorities != null) {
            return authorities;
        }
        //采用stream流的方式将权限字符串集合转化为SimpleGrantedAuthority类型的集合
        authorities = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return authorities;
    }

    /**
     * @Description: 用户的密码（加密后）
     * @param:
     * @date: 2024/3/23 21:52
     */

    @Override
    public String getPassword() {
        return user.getuPassword();
    }

    /**
     * @Description: 用户账号（唯一）
     * @param:
     * @date: 2024/3/23 21:53
     */

    @Override
    public String getUsername() {
        return user.getuAccountId();
    }

    /**
     * @Description: 用户的账号是否过期
     * @param:
     * @date: 2024/3/23 21:53
     */

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * @Description: 用户的账号是否被锁定
     * @param:
     * @date: 2024/3/23 21:54
     */

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * @Description: 用户的凭据（密码）是否已过期
     * @param:
     * @date: 2024/3/23 21:54
     */

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * @Description: 用户是否启用
     * @param:
     * @date: 2024/3/23 21:54
     */

    @Override
    public boolean isEnabled() {
        return true;
    }
}
