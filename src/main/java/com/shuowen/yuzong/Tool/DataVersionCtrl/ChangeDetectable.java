package com.shuowen.yuzong.Tool.DataVersionCtrl;


import java.util.List;

/**
 * 接口，只有实现了这个接口才能版本管理
 */
public interface ChangeDetectable<T>
{

    /**
     * 判断当前对象是否为新增项
     * 通常检查数据库生成的字段是否为null或默认值
     */
    boolean isNewItem();

    /**
     * 比较两个对象，返回变更的字段列表
     *
     * @param other 另一个对象（通常是原对象）
     * @return 变更的字段名称列表，如果没有变更返回空列表
     */
    List<String> getChangedFields(T other);

    /**
     * 获取对象的唯一标识
     */
    Object getUniqueKey();
}