package com.travel.controller;

import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyAuthority('test')")
    public String hello() {
        return "hello world 你爹";
    }
}
