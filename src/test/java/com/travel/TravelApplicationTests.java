package com.travel;

import com.travel.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class TravelApplicationTests {

    @Autowired
    private UserService userService;

    @Test
    void contextLoads() {

    }

    @Test
    public void TestBCryptPasswordEncoder() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode1 = encoder.encode("1234");
        String encode2 = encoder.encode("1234");
        System.out.println(encode1);
        System.out.println(encode2);
    }

}
