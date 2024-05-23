package com.travel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.common.ResponseResult;
import com.travel.entity.User;
import com.travel.entity.vo.LoginByIdVo;
import com.travel.entity.vo.LoginByPhoneVo;
import com.travel.entity.vo.UserRegistVo;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public interface UserService extends IService<User> {

    ResponseResult<String> regist(UserRegistVo user);

    ResponseResult<Object> verifyUserName(String username);

    ResponseResult<HashMap<String, Object>> loginByUserName(LoginByIdVo user);

    ResponseResult<HashMap<String, Object>> loginByPhone(LoginByPhoneVo loginByPhoneVo);

    ResponseResult<String> echoLogin(String username);

}
