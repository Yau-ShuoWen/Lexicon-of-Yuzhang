package com.shuowen.yuzong.Tool.DataVersionCtrl;

import lombok.Data;

import java.util.*;

@Data
public class ChangeResult<T extends ChangeDetectable<T>>
{
    private ChangeType changeType;
    private T oldItem;
    private T newItem;
    private List<String> changedFields;

    private T previousItem;   // 前一项
    private T nextItem;       // 后一项
    private Integer position; // 在列表中的位置

    public ChangeResult(ChangeType changeType, T oldItem, T newItem)
    {
        this.changeType = changeType;
        this.oldItem = oldItem;
        this.newItem = newItem;
    }
}