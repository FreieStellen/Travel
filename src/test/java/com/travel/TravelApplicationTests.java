package com.travel;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.travel.entity.Review;
import com.travel.entity.Scency;
import com.travel.entity.vo.SelectRandomVo;
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

        List<SelectRandomVo> collect = scencyService.lambdaQuery().orderByDesc(Scency::getUpdateTime)
                .last("LIMIT 6").list()
                .stream().map(res -> {
                    SelectRandomVo selectRandomVo = new SelectRandomVo();
                    selectRandomVo.setId(res.getId().toString());
                    selectRandomVo.setName(res.getName());
                    selectRandomVo.setImage(res.getImage());

                    double average = reviewService.listObjs(new LambdaQueryWrapper<Review>()
                                    .eq(Review::getScencyId, res.getId())
                                    .isNull(Review::getBelongId)
                                    .select(Review::getScore))
                            .stream().mapToDouble(var -> (float) var).average().orElse(0);

                    double score = (Math.round(average * 10));
                    score /= 10;
                    selectRandomVo.setScore(score);
                    return selectRandomVo;
                }).collect(Collectors.toList());

        SelectRandomVo[][] selectRandomVos = new SelectRandomVo[2][3];

        SelectRandomVo[] randomVos = collect.toArray(new SelectRandomVo[0]);
        int a = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                selectRandomVos[i][j] = randomVos[a++];
            }
        }
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.println(selectRandomVos[i][j]);
            }
            System.out.println();
        }
    }

}
