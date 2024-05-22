package com.travel.entity.dto;

import com.travel.entity.Package;
import com.travel.entity.PackageScency;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/*
 *@ClassName PackageDto 套餐景点中间表
 *@Author Freie  stellen
 *@Date 2024/5/21 17:25
 */
//调用父类的EqualsAndHashCode方法
@EqualsAndHashCode(callSuper = true)
@Data
public class PackageDto extends Package {

    private List<PackageScency> packageScencies = new ArrayList<>();

}
