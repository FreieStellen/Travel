package com.travel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.common.ResponseResult;
import com.travel.entity.District;
import org.springframework.stereotype.Service;

@Service
public interface DistrictService extends IService<District> {
    ResponseResult<String> add(District district);

    ResponseResult<Page<District>> selectList();
}
