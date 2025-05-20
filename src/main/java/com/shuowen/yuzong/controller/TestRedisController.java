package com.shuowen.yuzong.controller;

import com.shuowen.yuzong.redis.RedisDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class TestRedisController {

    @Autowired
    private RedisDao redisDao;

    @RequestMapping("/api/redis/set")
    public Object setKV(@RequestParam String key,@RequestParam String value){
        boolean flg = redisDao.set(key,value);
        return flg;
    }
}
