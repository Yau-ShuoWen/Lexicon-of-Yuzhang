package com.shuowen.yuzong.data.domain.Reference;

import com.shuowen.yuzong.Tool.FractionIndex;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ObjectTool;
import com.shuowen.yuzong.Tool.Obfuscation;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.data.model.Reference.RefEntity;
import lombok.Data;

import java.util.*;

@Data
public class RefPage
{
    private String dictionary;
    private String frontSort;
    private String endSort;
    private String content;
    private Integer page;

    public RefPage()
    {
    }

    public RefPage(List<RefEntity> list)
    {
        if (list.size() < 2) throw new IllegalStateException("页面结构异常，记录数量少于2（至少需要开头结尾的两个标记）");
        if (!ObjectTool.allEqual(list, RefEntity::getDictionary) || !ObjectTool.allEqual(list, RefEntity::getPage))
            throw new IllegalStateException("页面结构异常，页码或者字典不一致");

        var l = new ArrayList<>(list); // 创建新的对象是因为其他地方可能是不可变的数组
        l.sort(Comparator.comparing(RefEntity::getSort));

        var front = l.get(0);
        var end = l.get(l.size() - 1);
        if (!front.getContent().equals(Keyword.FRONT_OF_PAGE) || !end.getContent().equals(Keyword.END_OF_PAGE))
            throw new IllegalStateException("页面结构异常，开头和结尾的标记不正确");

        frontSort = front.getSort();
        endSort = end.getSort();
        dictionary = front.getDictionary();  // 已经被证明全部相等，直接获取即可
        page = front.getPage();
        content = "";
        for (int i = 1; i < l.size() - 1; i++)
        {
            if (i != 1) content += "\n\n";
            content += l.get(i).getContent();
        }
    }

    public Pair<List<RefEntity>, List<RefEntity>> transfer()
    {
        // 页面开始和结束部分，页数的边界部分
        var edge = List.of(
                new RefEntity(dictionary, frontSort, Keyword.FRONT_OF_PAGE, page),
                new RefEntity(dictionary, endSort, Keyword.END_OF_PAGE, page)
        );

        // 中段，内容部分
        var mid = new ArrayList<RefEntity>();
        var texts = content.split("\n\n");
        var sorts = FractionIndex.between(FractionIndex.of(frontSort), FractionIndex.of(endSort), texts.length);
        for (int i = 0; i < sorts.size(); i++)
            mid.add(new RefEntity(dictionary, sorts.get(i).toString(), texts[i], page));

        return Pair.of(edge, mid);
    }

    private RefPage(String dictionary, String frontSort, String endSort, String content, Integer page)
    {
        this.dictionary = dictionary;
        this.frontSort = frontSort;
        this.endSort = endSort;
        this.content = content;
        this.page = page;
    }

    public RefPage encode()
    {
        return new RefPage(dictionary, Obfuscation.encode(frontSort),
                Obfuscation.encode(endSort), content, page);
    }

    public RefPage decode()
    {
        return new RefPage(dictionary, Obfuscation.decode(frontSort),
                Obfuscation.decode(endSort), content, page);
    }
}
