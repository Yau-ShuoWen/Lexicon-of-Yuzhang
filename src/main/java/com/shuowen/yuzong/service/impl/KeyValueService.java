package com.shuowen.yuzong.service.impl;

import com.shuowen.yuzong.data.mapper.KVSMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KeyValueService
{
    @Autowired
    KVSMapper m;

    private static KeyValueService instance;

    @PostConstruct
    public void init()
    {
        instance = this;
    }

    public static String get(String key)
    {
        return instance.m.get(key);
    }

    public static boolean set(String key, String value)
    {
        instance.m.del(key);
        instance.m.set(key, value);
        return true;
    }

    public static void del(String key)
    {
        instance.m.del(key);
    }
}
