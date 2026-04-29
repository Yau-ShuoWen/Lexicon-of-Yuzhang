package com.shuowen.yuzong.Tool.format;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class JsonTool
{
    private JsonTool()
    {
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static <T> T readJson(String json, TypeReference<T> typeRef)
    {
        if (json == null || json.isEmpty())
        {
            return getEmptyInstance(typeRef);
        }
        try
        {
            return MAPPER.readValue(json, typeRef);
        } catch (Exception e)
        {
            System.out.println("JSON 解析失败，获得空对象");
            return getEmptyInstance(typeRef);
        }
    }


    /**
     * 根据TypeReference的类型返回对应的空实例
     */
    @SuppressWarnings ("unchecked")
    private static <T> T getEmptyInstance(TypeReference<T> typeRef)
    {
        Type type = typeRef.getType();

        // 处理ParameterizedType（如List<String>, Map<String, Object>等）
        if (type instanceof ParameterizedType)
        {
            ParameterizedType paramType = (ParameterizedType) type;
            Class<?> rawType = (Class<?>) paramType.getRawType();

            if (List.class.isAssignableFrom(rawType))
            {
                return (T) Collections.emptyList();
            }
            else if (Map.class.isAssignableFrom(rawType))
            {
                return (T) Collections.emptyMap();
            }
        }

        // 处理普通Class类型
        if (type instanceof Class)
        {
            Class<?> clazz = (Class<?>) type;
            if (List.class.isAssignableFrom(clazz))
            {
                return (T) Collections.emptyList();
            }
            else if (Map.class.isAssignableFrom(clazz))
            {
                return (T) Collections.emptyMap();
            }
        }

        // 对于其他类型，返回null（如普通对象类型）
        return null;
    }

    /**
     * 对象转 JSON 字符串
     */
    public static String toJson(Object obj)
    {
        if (obj == null) return "null";
        try
        {
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e)
        {
            System.out.println("对象转 JSON 失败，获得字符串\"null\"");
            return "null";
        }
    }

    /**
     * 对象转 JSON 字符串，如果解析失败，使用默认的
     */
    public static String toJson(Object obj, String backup)
    {
        if (obj == null) return backup;
        try
        {
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e)
        {
            System.out.println("对象转 JSON 失败，转换成默认结构");
            return backup;
        }
    }
}