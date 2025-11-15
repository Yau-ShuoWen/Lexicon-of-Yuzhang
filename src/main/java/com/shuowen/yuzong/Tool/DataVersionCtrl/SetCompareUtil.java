package com.shuowen.yuzong.Tool.DataVersionCtrl;

import java.util.*;

/**
 * 比较集合差异，适用于实现了ChangeDetectable接口的对象
 * @apiNote 要实现 {@code ChangeDetectable} 的三个函数以及{@code equals}的函数
 */

public class SetCompareUtil
{
    public static <T extends ChangeDetectable<T>> List<ChangeResult<T>>
    compare(Set<T> oldSet, Set<T> newSet)
    {
        List<ChangeResult<T>> results = new ArrayList<>();

        Map<Object, T> oldMap = toMapByKey(oldSet);
        Map<Object, T> newMap = toMapByKey(newSet);


        for (T oldItem : oldSet)
        {
            Object key = oldItem.getUniqueKey();
            if (!newMap.containsKey(key))
            {
                results.add(new ChangeResult<>(ChangeType.DELETED, oldItem, null));
            }
            else
            {
                T newItem = newMap.get(key);
                List<String> changedFields = newItem.getChangedFields(oldItem);
                if (!changedFields.isEmpty())
                {
                    ChangeResult<T> result = new ChangeResult<>(ChangeType.MODIFIED, oldItem, newItem);
                    result.setChangedFields(changedFields);
                    results.add(result);
                }
            }
        }

        for (T newItem : newSet)
        {
            Object key = newItem.getUniqueKey();
            if (!oldMap.containsKey(key))
            {
                results.add(new ChangeResult<>(ChangeType.ADDED, null, newItem));
            }
        }
        return results;
    }

    /**
     * 将 Set 转换为 Map，key 为对象的唯一标识
     */
    private static <T extends ChangeDetectable<T>> Map<Object, T> toMapByKey(Set<T> set)
    {
        Map<Object, T> map = new HashMap<>();
        for (T item : set)
        {
            map.put(item.getUniqueKey(), item);
        }
        return map;
    }
}
