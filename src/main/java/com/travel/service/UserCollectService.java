package com.travel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.common.ResponseResult;
import com.travel.entity.UserCollect;
import com.travel.entity.vo.UserCollectVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserCollectService extends IService<UserCollect> {
    ResponseResult<List<UserCollectVo>> selectCollect();

}
