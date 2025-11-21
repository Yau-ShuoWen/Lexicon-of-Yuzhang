package com.shuowen.yuzong.Tool.JavaUtilExtend;

import java.util.*;

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
}
