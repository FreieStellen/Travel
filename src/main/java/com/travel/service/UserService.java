package com.travel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.common.ResponseResult;
import com.travel.entity.User;
import com.travel.entity.dto.LoginByIdDto;
import com.travel.entity.dto.LoginByPhoneDto;
import com.travel.entity.dto.UserRegistDto;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public interface UserService extends IService<User> {

    ResponseResult<String> regist(UserRegistDto user);

    ResponseResult<Object> verifyUserName(String username);

    ResponseResult<HashMap<String, Object>> loginByUserName(LoginByIdDto user);

    ResponseResult<HashMap<String, Object>> loginByPhone(LoginByPhoneDto loginByPhoneDto);

    ResponseResult<String> echoLogin(String username);

}
