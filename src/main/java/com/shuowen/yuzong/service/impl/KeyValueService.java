package com.shuowen.yuzong.service.impl;

import com.shuowen.yuzong.data.mapper.KVSMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KeyValueService
{
    @Autowired
    KVSMapper m;

    public boolean set(String key, String value)
    {
        m.del(key);
        m.set(key, value);
        return true;
    }

    public String get(String key)
    {
        return m.get(key);
    }
}
