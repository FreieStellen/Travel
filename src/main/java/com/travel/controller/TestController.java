package com.travel.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 *@ClassName TestController
 *@Author Freie  stellen
 *@Date 2024/4/5 16:22
 */
@RestController
public class TestController {

    @RequestMapping("/hello")
    public String hello() {
        return "hello world 你爹";
    }
}
