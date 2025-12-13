package com.shuowen.yuzong.Tool.JavaUtilExtend;

import java.util.*;
import java.util.function.Function;

public class MapTool
{
    /**
     * 更换Map的键名
     *
     * @param map    Map对象
     * @param oldKey 旧键名
     * @param newKey 新键名
     * @return 是否成功更换
     */
    public static <K, V> boolean renameKey(Map<K, V> map, K oldKey, K newKey)
    {
        if (map.containsKey(oldKey))
        {
            V value = map.get(oldKey);
            map.put(newKey, value);
            map.remove(oldKey);
            return true;
        }
        return false;
    }

    /**
     * 批量更换键名
     *
     * @param map         Map对象
     * @param keyMappings 键名映射关系
     */
    public static <K, V> void renameKeys(Map<K, V> map, Map<K, K> keyMappings)
    {
        for (Map.Entry<K, K> entry : keyMappings.entrySet())
        {
            renameKey(map, entry.getKey(), entry.getValue());
        }
    }

    /**
     * 在 getOrDefault 上面，对于符合的情况做一个进步操作，默认值不变，无论如何不会影响原map
     *
     * @param map          Map对象
     * @param key          旧键名
     * @param operation    返回自身操作的函数
     * @param defaultValue 没有的时候的直接返回值
     */
    public static <K, V> V getOrDefault(Map<K, V> map, K key, Function<V, V> operation, V defaultValue)
    {
        V value = map.get(key);
        return (value == null) ? defaultValue : operation.apply(value);
    }
}
