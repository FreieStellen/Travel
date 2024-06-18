package com.travel;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.travel.entity.District;
import com.travel.entity.PackageDistrict;
import com.travel.mapper.DistrictMapper;
import com.travel.mapper.PackageDistrictMapper;
import com.travel.mapper.PackageMapper;
import com.travel.service.*;
import com.travel.utils.RedisCache;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

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

        String packageId = "1792481230557470130";
        List<Object> districtList = packageDistrictService.listObjs(new LambdaQueryWrapper<PackageDistrict>()
                        .eq(PackageDistrict::getPackageId, packageId)
                        .select(PackageDistrict::getDistrictId))
                .stream().map(res ->
                        districtService.listObjs(new LambdaQueryWrapper<District>()
                                .eq(District::getId, res).select(District::getName)).stream().distinct().collect(Collectors.toList())
                ).collect(Collectors.toList());

        System.out.println(districtList);
    }

}
