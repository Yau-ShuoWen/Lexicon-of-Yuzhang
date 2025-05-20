package com.shuowen.yuzong.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    // http://localhost:8088/api/test
    @RequestMapping("/api/test")
    public String test(){
        return "Hello World";
    }

    // http://localhost:8088/api/test/param?name=budou
    @RequestMapping("/api/test/param")
    public String testParam(@RequestParam String name){
        return "你好 , " + name;
    }
}
