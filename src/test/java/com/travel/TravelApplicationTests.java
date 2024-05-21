package com.travel;

import com.travel.service.UserService;
import com.travel.utils.PasswordEncoder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TravelApplicationTests {

    @Autowired
    private UserService userService;

    @Test
    void contextLoads() {

    }

    @Test
    public void TestBCryptPasswordEncoder() {
        String password = "1234";


        String encode = PasswordEncoder.encode(password);
        System.out.println(encode);
        System.out.println(PasswordEncoder.matches("@XWX-xu6oyb258971hqsj49qi-58cfe640850cf4ff14002f083ae2ff38"
                , password));
    }

}
