package com.travel;

import cn.hutool.core.bean.BeanUtil;
import com.travel.entity.Scency;
import com.travel.entity.vo.PopularVo;
import com.travel.mapper.PackageMapper;
import com.travel.service.PackageService;
import com.travel.service.ScencyService;
import com.travel.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class TravelApplicationTests {


    @Resource
    private UserService userService;
    @Autowired
    private PackageService packageService;

    @Autowired
    private PackageMapper packageMapper;

    @Autowired
    private ScencyService scencyService;

    @Test
    void contextLoads() {

    }

    @Test
    public void TestBCryptPasswordEncoder() {

        PopularVo popularVo = new PopularVo();
        Scency scency = scencyService.lambdaQuery().orderByDesc(Scency::getLiked).last("LIMIT 1").one();
        BeanUtil.copyProperties(scency, popularVo, false);
        System.out.println(popularVo);
    }

}
