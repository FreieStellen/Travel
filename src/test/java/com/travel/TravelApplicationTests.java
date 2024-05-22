package com.travel;

import com.travel.entity.Package;
import com.travel.mapper.PackageMapper;
import com.travel.service.PackageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
class TravelApplicationTests {

    @Autowired
    private PackageService packageService;

    @Autowired
    private PackageMapper packageMapper;

    @Test
    void contextLoads() {

    }

    @Test
    public void TestBCryptPasswordEncoder() {
        Package aPackage = new Package();
    
        aPackage.setCategoryId(1793079464066859010L);
        aPackage.setName("吴寰宇徐扬套餐");
        aPackage.setSpecial("sb");
        aPackage.setGrade(BigDecimal.valueOf(2.0));
        aPackage.setDiscribe("蠢");
        aPackage.setTicketNum(20);
        aPackage.setScore("4.5");
        aPackage.setGradeDiscribe("....");

        packageMapper.insert(aPackage);

    }

}
