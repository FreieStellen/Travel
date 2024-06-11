package com.travel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.common.ResponseResult;
import com.travel.entity.Package;
import com.travel.entity.dto.PackageDto;
import com.travel.entity.vo.PopularVo;
import com.travel.entity.vo.SelectRandomVo;
import com.travel.entity.vo.ShowInfoVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PackageService extends IService<Package> {
    ResponseResult<String> add(PackageDto packageDto);

    ResponseResult<ShowInfoVo> selectPackageById(Long id);

    ResponseResult<String> likePackage(Long id);

    ResponseResult<Page<Package>> pagePackage(int page, int pageSize);

    ResponseResult<PopularVo> popular();

    ResponseResult<List<SelectRandomVo>> selectRandom();

}
