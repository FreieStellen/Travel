package com.travel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.ResponseResult;
import com.travel.entity.District;
import com.travel.mapper.DistrictMapper;
import com.travel.service.DistrictService;
import com.travel.utils.CacheClient;
import com.travel.utils.RedisCache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.travel.utils.RedisConstants.DISTRICT_CODE_KEY;
import static com.travel.utils.RedisConstants.DISTRICT_TTL_DAYS;

/*
 *@ClassName DistrictServiceImpl 分类实现类
 *@Author Freie  stellen
 *@Date 2024/5/20 16:35
 */
@Service
public class DistrictServiceImpl extends ServiceImpl<DistrictMapper, District> implements DistrictService {

    @Resource
    private RedisCache redisCache;

    @Resource
    private CacheClient cacheClient;

    /**
     * @Description: 添加分类
     * @param: category
     * @date: 2024/5/20 16:51
     */

    @Transactional
    @Override
    public ResponseResult<String> add(District district) {

        //1.添加数据库
        boolean save = save(district);

        //1.1判断是否添加成功
        if (save) {
            return ResponseResult.error("添加失败！");
        }
        //2.数据库添加成功将数据添加到redis中
        String id = district.getId().toString();
        //2.1自定义map集合
        HashMap<String, District> map = new HashMap<>();
        //2.2存到缓存中并设置过期时间
        String key = DISTRICT_CODE_KEY;
        map.put(id, district);
        redisCache.setCacheMap(key, map);
        redisCache.expire(key, DISTRICT_TTL_DAYS, TimeUnit.DAYS);

        return ResponseResult.success("添加成功！");
    }

    /**
     * @Description: 查询地区表
     * @date: 2024/5/20 17:20
     */

    @Transactional
    @Override
    public ResponseResult<List<District>> selectList() {

        List<District> list = cacheClient.selectDisAll();

        if (list.isEmpty()) {
            return ResponseResult.error("数据为空");
        }
        return ResponseResult.success(list);
    }
}
