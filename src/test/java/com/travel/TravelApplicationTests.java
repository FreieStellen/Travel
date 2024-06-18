package com.travel;

import com.travel.common.CommonHolder;
import com.travel.mapper.DistrictMapper;
import com.travel.mapper.PackageDistrictMapper;
import com.travel.mapper.PackageMapper;
import com.travel.service.*;
import com.travel.utils.RedisCache;
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

    @Autowired
    private RedisCache redisCache;

    @Resource
    private DistrictService districtService;

    @Resource
    private ReviewService reviewService;

    @Resource
    private DistrictMapper districtMapper;
    @Resource
    private PackageDistrictMapper packageDistrictMapper;

    @Resource
    private PackageDistrictService packageDistrictService;

    @Test
    void contextLoads() {

    }

    @Test
    public void TestBCryptPasswordEncoder() {

        String user = CommonHolder.getUser();
        System.out.println(user);
    }

}
