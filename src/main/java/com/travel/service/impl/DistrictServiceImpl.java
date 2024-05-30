package com.travel.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.ResponseResult;
import com.travel.entity.District;
import com.travel.mapper.DistrictMapper;
import com.travel.service.DistrictService;
import org.springframework.stereotype.Service;

/*
 *@ClassName CategoryServiceImpl 分类实现类
 *@Author Freie  stellen
 *@Date 2024/5/20 16:35
 */
@Service
public class DistrictServiceImpl extends ServiceImpl<DistrictMapper, District> implements DistrictService {

    /**
     * @Description: 添加分类
     * @param: category
     * @date: 2024/5/20 16:51
     */

    @Override
    public ResponseResult<String> add(District district) {

        boolean save = save(district);

        if (save) {
            return ResponseResult.success("添加成功！");
        }
        return ResponseResult.error("添加失败！");
    }

    /**
     * @Description: 分页查询
     * @date: 2024/5/20 17:20
     */

    @Override
    public ResponseResult<Page<District>> selectList() {


        Page<District> page = query().orderByAsc("sort").page(new Page<>(0, 5));


        return ResponseResult.success(page);
    }
}
