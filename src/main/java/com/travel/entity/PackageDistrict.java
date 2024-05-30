package com.travel.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/*
 *@ClassName PackageDistrict
 *@Author Freie  stellen
 *@Date 2024/5/28 17:24
 */
@Data
public class PackageDistrict {

    //解决反序列化兼容问题
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 地区id
     */
    private Long districtId;

    /**
     * 套餐id
     */
    private Long packageId;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
