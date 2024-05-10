package com.travel.service;

import com.travel.common.ResponseResult;
import com.travel.entity.vo.LoginByIdVo;
import com.travel.entity.vo.LoginByPhoneVo;

import java.util.HashMap;

public interface LoginService {
    ResponseResult<HashMap<String, Object>> loginByUserName(LoginByIdVo user);

    ResponseResult<String> logout();

    ResponseResult<HashMap<String, Object>> loginByPhone(LoginByPhoneVo loginByPhoneVo);
}
