package com.travel.utils;

/*
 *@ClassName DataBaseControl
 *@Author Freie  stellen
 *@Date 2024/6/6 16:59
 */

import com.travel.entity.District;
import com.travel.entity.PackageDistrict;
import com.travel.service.DistrictService;
import com.travel.service.PackageDistrictService;
import com.travel.service.ReviewService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Component
public class DataBaseControl {

    @Lazy
    @Resource
    private DistrictService districtService;

    @Resource
    private ReviewService reviewService;

    @Resource
    private PackageDistrictService packageDistrictService;

    public List<District> selectDisAll() {
        List<District> list = districtService.lambdaQuery().list();
        if (list.isEmpty()) {
            throw new RuntimeException("...");
        }
        return list;
    }

    /**
     * @Description: 查询景点时添加地区名称
     * @param: id
     * @date: 2024/6/6 21:11
     */

    public District selectDistrictName(Long id) {
        District district = districtService.lambdaQuery().eq(District::getId, id).one();
        if (Objects.isNull(district)) {
            throw new RuntimeException("...");
        }
        return district;
    }

    /**
     * @Description: 查询套餐时添加包含的地区
     * @param:
     * @date: 2024/6/6 21:13
     */

    public PackageDistrict selectPackDistrictName(Long id) {
        PackageDistrict one = packageDistrictService.lambdaQuery().eq(PackageDistrict::getDistrictId, id).one();
        return one;
    }


    public int selectReviewNum(Long id) {

        return 0;
    }
}
