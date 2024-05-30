package com.travel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.common.ResponseResult;
import com.travel.entity.Package;
import com.travel.entity.dto.PackageDto;
import org.springframework.stereotype.Service;

@Service
public interface PackageService extends IService<Package> {
    ResponseResult<String> add(PackageDto packageDto);

    ResponseResult<Package> selectPackageById(Long id);

    ResponseResult<String> likePackage(Long id);

    ResponseResult<Page<Package>> pagePackage(int page, int pageSize);
}
