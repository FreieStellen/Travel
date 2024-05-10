package com.travel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.common.ResponseResult;
import com.travel.entity.Manager;
import com.travel.entity.vo.LoginByIdVo;
import com.travel.entity.vo.LoginByPhoneVo;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public interface ManagerService extends IService<Manager> {

    ResponseResult<HashMap<String, String>> loginNumber(LoginByIdVo loginByIdVo);

    ResponseResult<Manager> loginPhone(LoginByPhoneVo manager);
}
