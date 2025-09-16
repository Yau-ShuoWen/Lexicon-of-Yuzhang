package com.shuowen.yuzong.controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 基礎测试网络连通，可以在测试网络连接的时候使用，给出了示例的网络接口
 */

@RestController
@CrossOrigin (origins = "*")
@RequestMapping ("/api")
public class HelloController
{
    // http://localhost:8080/api/test
    @RequestMapping ("/test")
    public String test()
    {
        return "你好，世界";
    }

    // http://localhost:8080/api/test/param?name=shuowen
    @RequestMapping ("/test/param")
    public String testParam(@RequestParam String name)
    {
        return "你好，" + name;
    }

    // http://localhost:8080/api/hello
    @RequestMapping ("/hello")
    public Map<String, String> hello()
    {
        return Map.of("message", "你好，世界");
    }

    // http://localhost:8080/api/hello?name=shuowen
    @RequestMapping ("/hello/param")
    public Map<String, String> helloParam(@RequestParam String name)
    {
        return Map.of("message", "你好，" + name);
    }
}
