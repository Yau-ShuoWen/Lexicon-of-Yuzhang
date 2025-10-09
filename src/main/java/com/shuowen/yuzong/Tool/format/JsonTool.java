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
    /**
     * 解析 JSON 为对象
     */
    public static <T> T safeRead(String json, Class<T> clazz, ObjectMapper mapper)
    {
        if (json == null || json.isEmpty()) return null;
        try
        {
            return mapper.readValue(json, clazz);
        } catch (Exception e)
        {
            System.out.println("JSON 解析失败，得到null");
            //System.err.println("JSON 解析失败：" + e.getMessage());
            return null;
        }
    }

    /**
     * 解析 JSON 为泛型对象（List/Map 等）
     * 自动处理空字符串和解析失败：
     * - JSON "" 或 null → List 返回空集合，Map 返回空 Map
     * - 解析失败时同样返回对应的空集合或空Map
     */
    public static <T> T readJson(String json, TypeReference<T> typeRef, ObjectMapper mapper)
    {
        if (json == null || json.isEmpty())
        {
            return getEmptyInstance(typeRef);
        }
        try
        {
            return mapper.readValue(json, typeRef);
        } catch (Exception e)
        {
            System.out.println("JSON 解析失败，获得空对象");
            //System.err.println("JSON 解析失败：" + e.getMessage());
            // 出错时返回对应的空集合或空Map
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
    public static String toJson(Object obj, ObjectMapper mapper)
    {
        if (obj == null) return "null";
        try
        {
            return mapper.writeValueAsString(obj);
        } catch (Exception e)
        {
            System.out.println("对象转 JSON 失败，获得字符串\"null\"");
            //System.err.println("对象转 JSON 失败：" + e.getMessage());
            return "null";
        }
    }

    public static String toJson(Object obj, ObjectMapper mapper, String backup)
    {
        if (obj == null) return backup;
        try
        {
            return mapper.writeValueAsString(obj);
        } catch (Exception e)
        {
            System.out.println("对象转 JSON 失败，转换成默认结构");
            //System.err.println("对象转 JSON 失败：" + e.getMessage());
            return backup;
        }
    }
}