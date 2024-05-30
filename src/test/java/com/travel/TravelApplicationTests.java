package com.travel;

import com.travel.entity.User;
import com.travel.mapper.PackageMapper;
import com.travel.service.PackageService;
import com.travel.service.UserService;
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

    @Test
    void contextLoads() {

    }

    @Test
    public void TestBCryptPasswordEncoder() {
        User one =
                userService.lambdaQuery()
                        .eq(User::getAccountId, "邢哥肌肉大邢哥肌肉大")
                        .eq(User::getStatus, 1)
                        .one();
        System.out.println(one);
    }

}
