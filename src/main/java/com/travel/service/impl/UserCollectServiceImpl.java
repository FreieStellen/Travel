package com.travel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.CommonHolder;
import com.travel.common.ResponseResult;
import com.travel.entity.Package;
import com.travel.entity.Scency;
import com.travel.entity.UserCollect;
import com.travel.entity.vo.UserCollectVo;
import com.travel.mapper.UserCollectMapper;
import com.travel.service.PackageService;
import com.travel.service.ScencyService;
import com.travel.service.UserCollectService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/*
 *@ClassName UserCollectServiceImpl
 *@Author Freie  stellen
 *@Date 2024/5/23 12:00
 */

@Service
public class UserCollectServiceImpl extends ServiceImpl<UserCollectMapper, UserCollect> implements UserCollectService {

    @Lazy
    @Resource
    private ScencyService scencyService;

    @Lazy
    @Resource
    private PackageService packageService;

    @Override
    public ResponseResult<List<UserCollectVo>> selectCollect() {

        List<UserCollectVo> list = lambdaQuery().eq(UserCollect::getUserId, CommonHolder.getUser()).list()
                .stream().map(res -> {
                    UserCollectVo collectVo = new UserCollectVo();

                    if (res.getScencyId() == null) {
                        Package one = packageService.lambdaQuery().eq(Package::getId, res.getPackageId())
                                .one();
                        collectVo.setPackageId(res.getPackageId().toString());
                        collectVo.setName(one.getName());
                        collectVo.setImg(one.getImage());
                    } else {
                        Scency one = scencyService.lambdaQuery().eq(Scency::getId, res.getScencyId())
                                .one();
                        collectVo.setScencyId(res.getScencyId().toString());
                        collectVo.setName(one.getName());
                        collectVo.setImg(one.getImage());
                    }
                    return collectVo;
                }).collect(Collectors.toList());

        return ResponseResult.success(list);
    }
}
