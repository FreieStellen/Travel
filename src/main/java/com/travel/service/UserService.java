package com.travel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.common.ResponseResult;
import com.travel.entity.User;
import com.travel.entity.dto.LoginByIdDto;
import com.travel.entity.dto.LoginByPhoneDto;
import com.travel.entity.dto.UserRegistDto;
import com.travel.entity.vo.LoginUserVo;
import org.springframework.stereotype.Service;

@Service
public interface UserService extends IService<User> {

    ResponseResult<String> regist(UserRegistDto user);

    ResponseResult<Object> verifyUserName(String username);

    ResponseResult<LoginUserVo> loginByUserName(LoginByIdDto user);

    ResponseResult<LoginUserVo> loginByPhone(LoginByPhoneDto loginByPhoneDto);

    ResponseResult<String> echoLogin(String username);

    ResponseResult<User> SelectById(Long id, String username);

}
