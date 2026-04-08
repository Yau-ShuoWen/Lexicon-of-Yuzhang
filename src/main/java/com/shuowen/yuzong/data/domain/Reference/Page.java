package com.shuowen.yuzong.data.domain.Reference;

import com.shuowen.yuzong.Tool.JavaUtilExtend.ObjectTool;
import com.shuowen.yuzong.data.model.Reference.RefEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
public abstract class Page
{
    String type;

    /**
     * @exception IllegalStateException 页面的结构有问题
     * */
    public static List<RefEntity> readList(List<RefEntity> list)
    {
        // 结构检查
        if (list.size() < 2) throw new IllegalStateException("页面结构异常，记录数量少于2（至少需要开头结尾的两个标记）");
        if (!ObjectTool.allEqual(list, RefEntity::getDictionary) || !ObjectTool.allEqual(list, RefEntity::getPageInfo))
            throw new IllegalStateException("页面结构异常，页码或者字典不一致");

        var l = new ArrayList<>(list); // 创建新的对象是因为其他地方可能是不可变的数组
        l.sort(Comparator.comparing(RefEntity::getSort));

        var front = l.get(0);
        var end = l.get(l.size() - 1);
        if (!front.getContent().equals(Keyword.FRONT_OF_PAGE) || !end.getContent().equals(Keyword.END_OF_PAGE))
            throw new IllegalStateException("页面结构异常，开头和结尾的标记不正确");

        return l;
    }
}
