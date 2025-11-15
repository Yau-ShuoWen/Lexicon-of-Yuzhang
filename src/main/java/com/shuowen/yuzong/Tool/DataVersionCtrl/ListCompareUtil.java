package com.shuowen.yuzong.Tool.DataVersionCtrl;

import java.util.*;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.*;

/**
 * 比较列表差异，特别为FractionalIndexing优化<p>
 * 适用于实现了ChangeDetectable接口的对象
 * @apiNote 要实现 {@code ChangeDetectable} 的三个函数以及{@code equals}的函数
 */

public class ListCompareUtil
{
    public static <T extends ChangeDetectable<T>> List<ChangeResult<T>> compare(List<T> oldList, List<T> newList)
    {
        List<ChangeResult<T>> results = new ArrayList<>();
        Patch<T> patch = DiffUtils.diff(oldList, newList);

        // 分析Delta变更
        for (AbstractDelta<T> delta : patch.getDeltas())
        {
            switch (delta.getType())
            {
                case DELETE-> handleDeletion(delta, results);
                case INSERT-> handleInsertion(delta, results, newList);
                case CHANGE -> handleChange(delta, results);
            }
        }
        return results;
    }

    private static <T extends ChangeDetectable<T>> void handleDeletion(
            AbstractDelta<T> delta, List<ChangeResult<T>> results)
    {
        for (T deletedItem : delta.getSource().getLines())
        {
            results.add(new ChangeResult<>(ChangeType.DELETED, deletedItem, null));
        }
    }

    private static <T extends ChangeDetectable<T>> void handleInsertion(
            AbstractDelta<T> delta, List<ChangeResult<T>> results, List<T> newList)
    {
        int insertPosition = delta.getTarget().getPosition();
        List<T> insertedItems = delta.getTarget().getLines();

        for (int i = 0; i < insertedItems.size(); i++)
        {
            T newItem = insertedItems.get(i);

            // 使用接口方法判断是否为新增
            if (newItem.isNewItem())
            {
                T previousItem = getPreviousItem(newList, insertPosition + i);
                T nextItem = getNextItem(newList, insertPosition + i);

                ChangeResult<T> result = new ChangeResult<>(ChangeType.ADDED, null, newItem);
                result.setPreviousItem(previousItem);
                result.setNextItem(nextItem);
                result.setPosition(insertPosition + i);

                results.add(result);
            }
        }
    }

    private static <T extends ChangeDetectable<T>> void handleChange(
            AbstractDelta<T> delta, List<ChangeResult<T>> results)
    {
        List<T> originalItems = delta.getSource().getLines();
        List<T> newItems = delta.getTarget().getLines();

        for (int i = 0; i < Math.min(originalItems.size(), newItems.size()); i++)
        {
            T originalItem = originalItems.get(i);
            T newItem = newItems.get(i);

            // 使用接口方法获取变更字段
            List<String> changedFields = newItem.getChangedFields(originalItem);
            if (!changedFields.isEmpty())
            {
                ChangeResult<T> result = new ChangeResult<>(ChangeType.MODIFIED, originalItem, newItem);
                result.setChangedFields(changedFields);
                results.add(result);
            }
        }
    }

    /**
     * 获取指定位置的前一项
     */
    private static <T> T getPreviousItem(List<T> list, int currentPosition)
    {
        if (currentPosition > 0 && currentPosition < list.size())
        {
            return list.get(currentPosition - 1);
        }
        return null;
    }

    /**
     * 获取指定位置的后一项
     */
    private static <T> T getNextItem(List<T> list, int currentPosition)
    {
        if (currentPosition >= 0 && currentPosition < list.size() - 1)
        {
            return list.get(currentPosition + 1);
        }
        return null;
    }
}