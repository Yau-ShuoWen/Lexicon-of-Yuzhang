package com.shuowen.yuzong.Tool.JavaUtilExtend;

import java.lang.reflect.Array;
import java.util.*;
import java.util.Map;
import java.util.stream.Stream;

public class NullTool
{
    /**
     * 如果为null，返回默认值。默认值不能再为null了
     */
    public static <T> T getDefault(T obj, T defaultValue)
    {
        checkSingleNotNull(defaultValue, "默认值不能为空");
        return (obj == null) ? defaultValue : obj;
    }

    /**
     * 如果这一个元素为null，抛出默认异常
     */
    public static void checkSingleNotNull(Object obj)
    {
        if (obj == null) throw new NullPointerException();
    }

    /**
     * 如果这个元素为null，抛出msg异常
     */
    public static void checkSingleNotNull(Object obj, String msg)
    {
        if (obj == null) throw new NullPointerException(msg);
    }

    /**
     * 批量检查内容
     */
    public static void checkBatchNotNull(String message, Object... objects)
    {
        checkSingleNotNull(objects, message); // 直接传入null

        for (int i = 0; i < objects.length; i++)
        {
            checkObject(objects[i], i, message, new HashSet<>());
        }
    }

    /**
     * 递归检查对象及其内部元素
     */
    private static void checkObject(Object obj, int index, String message, Set<Object> visited)
    {
        checkSingleNotNull(obj, setMessage(message, index, "对象为空"));

        if (visited.contains(obj)) return;
        visited.add(obj);

        if (obj.getClass().isArray())
        {
            for (int i = 0; i < Array.getLength(obj); i++)
            {
                Object element = Array.get(obj, i);
                checkSingleNotNull(element, setMessage(message, index, String.format("数组第 %d 个元素为空", i + 1)));


                checkObject(element, index, message, visited);
            }
        }
        else if (obj instanceof Collection<?> collection)
        {
            int i = 0;
            for (Object element : collection)
            {
                checkSingleNotNull(element, setMessage(message, index, String.format("集合第 %d 个元素为空", i + 1)));
                checkObject(element, index, message, visited);
                i++;
            }
        }
        else if (obj instanceof Map<?, ?> map)
        {
            int i = 0;
            for (Map.Entry<?, ?> entry : map.entrySet())
            {
                Object key = entry.getKey();
                Object value = entry.getValue();

                checkSingleNotNull(key, setMessage(message, index, String.format("Map第 %d 个条目的键为空", i + 1)));
                checkSingleNotNull(value, setMessage(message, index, String.format("Map第 %d 个条目的值为空", i + 1)));

                checkObject(key, index, message, visited);
                checkObject(value, index, message, visited);

                i++;
            }
        }
        else if (obj instanceof Optional<?> optional)
        {
            if (optional.isEmpty())
                throw new NullPointerException(setMessage(message, index, "Optional为空"));
            else checkObject(optional.get(), index, message, visited);
        }
        else if (obj instanceof Stream)
            throw new IllegalArgumentException(setMessage(message, index, "不支持空检查"));

        // 可扩展
    }

    /**
     * 格式化错误消息
     */
    private static String setMessage(String baseMessage, int index, String detail)
    {
        StringBuilder sb = new StringBuilder();

        if (baseMessage != null && !baseMessage.isEmpty())
        {
            sb.append(baseMessage);
            sb.append(" - ");
        }

        sb.append(String.format("参数[%d]: %s", index + 1, detail));
        return sb.toString();
    }
}
