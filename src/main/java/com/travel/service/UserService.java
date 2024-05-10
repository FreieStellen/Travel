package com.travel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.common.ResponseResult;
import com.travel.entity.User;
import com.travel.entity.vo.UserRegistVo;
import org.springframework.stereotype.Service;

@Service
public interface UserService extends IService<User> {

    ResponseResult<User> regist(UserRegistVo user);

    ResponseResult<String> verifyUserName(String username);


}
