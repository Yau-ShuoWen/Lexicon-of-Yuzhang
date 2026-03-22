package com.shuowen.yuzong.Tool.JavaUtilExtend;

import java.util.*;
import java.util.function.Function;

public class MapTool
{
    private MapTool()
    {
    }

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
     * @param map    Map对象
     * @param mapper 键名映射函数
     */
    public static <K, V> void mappingKeyName(Map<K, V> map, Function<K, K> mapper)
    {
        Map<K, V> tmp = new HashMap<>();
        for (var i : map.entrySet()) tmp.put(mapper.apply(i.getKey()), i.getValue());
        map.clear();
        map.putAll(tmp);
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

    /**
     *
     */
    public static <K, V> Map<K, V> fromSet(Set<V> set, Function<V, K> getKey)
    {
        Map<K, V> map = new HashMap<>();
        for (var i : set) map.put(getKey.apply(i), i);
        return map;
    }

    public static <V> LinkedHashMap<String, V> orderMapOf(Object... kv)
    {
        LinkedHashMap<String, V> map = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) map.put(String.valueOf(kv[i]), (V) kv[i + 1]);
        return map;
    }

    public static <K, K1, V> Map<K1, V> mappingKey(Map<K, V> map, Function<K, K1> fun)
    {
        Map<K1, V> ans = new LinkedHashMap<>();
        for (var i : map.entrySet())
            ans.put(fun.apply(i.getKey()), i.getValue());
        return ans;
    }

    public static <K, V, V1> Map<K, V1> mappingValue(Map<K, V> map, Function<V, V1> fun)
    {
        Map<K, V1> ans = new LinkedHashMap<>();
        for (var i : map.entrySet())
            ans.put(i.getKey(), fun.apply(i.getValue()));
        return ans;
    }

    public static <K, V, K1, V1> Map<K1, V1> mapping(Map<K, V> map, Function<K, K1> fun1, Function<V, V1> fun2)
    {
        Map<K1, V1> ans = new LinkedHashMap<>();
        for (var i : map.entrySet())
            ans.put(fun1.apply(i.getKey()), fun2.apply(i.getValue()));
        return ans;
    }
}
