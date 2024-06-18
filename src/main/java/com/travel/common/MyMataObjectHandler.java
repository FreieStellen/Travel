package com.travel.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.travel.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/*
 *@ClassName MyMataObjectHandler 元数据对象处理器，在插入和更新数据时可以自动填充
 *@Author Freie  stellen
 *@Date 2024/3/28 9:54
 */
@Slf4j
@Component
public class MyMataObjectHandler implements MetaObjectHandler {

    @Autowired
    private RedisCache redisCache;

    /**
     * @Description: 在插入时自动填充字段
     * @param: metaObject
     * @date: 2024/3/28 10:04
     */

    @Override
    public void insertFill(MetaObject metaObject) {

        this.setFieldValByName("createTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("managerId", CommonHolder.getUser(), metaObject);

        log.info("--插入时字段--");
        log.info(metaObject.toString());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
    }
}
