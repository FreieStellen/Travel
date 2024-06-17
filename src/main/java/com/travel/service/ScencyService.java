package com.travel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.common.ResponseResult;
import com.travel.entity.Scency;
import com.travel.entity.vo.PopularVo;
import com.travel.entity.vo.SelectRandomVo;
import com.travel.entity.vo.ShowInfoVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ScencyService extends IService<Scency> {
    ResponseResult<String> add(Scency scency);

    ResponseResult<ShowInfoVo> selectScencyById(Long id);

    ResponseResult<String> likeScency(Long id);

    ResponseResult<PopularVo> popular();

    ResponseResult<SelectRandomVo[][]> selectRandom();

    ResponseResult<List<SelectRandomVo>> selectLike(String name);
}
