package com.shuowen.yuzong.Tool.format;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Json
{
    public static <T> T safeRead(String json, Class<T> clazz, ObjectMapper mapper)
    {
        if (json == null || json.isEmpty()) return null;
        try
        {
            return mapper.readValue(json, clazz);
        } catch (Exception e)
        {
            System.err.println("JSON 解析失敗：" + e.getMessage());
            return null;
        }
    }
}
