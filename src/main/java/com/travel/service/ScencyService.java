package com.travel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.common.ResponseResult;
import com.travel.entity.Scency;
import com.travel.entity.vo.PopularVo;
import org.springframework.stereotype.Service;

@Service
public interface ScencyService extends IService<Scency> {
    ResponseResult<String> add(Scency scency);

    ResponseResult<Scency> selectScencyById(Long id);

    ResponseResult<String> likeScency(Long id);

    ResponseResult<PopularVo> popular();

}
