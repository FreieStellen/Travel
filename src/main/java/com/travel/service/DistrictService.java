package com.travel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.common.ResponseResult;
import com.travel.entity.District;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DistrictService extends IService<District> {
    ResponseResult<String> add(District district);

    ResponseResult<List<District>> selectList();
}
