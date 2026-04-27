package com.shuowen.yuzong.data.domain.Reference;

import com.shuowen.yuzong.Tool.FractionIndex;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.TextTool.TextPinyinIPA;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Twin;
import com.shuowen.yuzong.data.model.Reference.RefEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 *
 */
@Data
@NoArgsConstructor
public class RefDraft extends Page
{
    private DictCode dictionary;
    private FractionIndex frontSort;
    private FractionIndex endSort;

    private String content;
    private Pair<String, Integer> pageInfo;

    private RefDraft(List<RefEntity> list)
    {
        type = "draft";

        var l = readList(list);
        var front = l.get(0);
        var end = l.get(l.size() - 1);

        // 标记只有sort字段重要
        frontSort = front.getTheSort();
        endSort = end.getTheSort();
        // 已经被证明全部相等，直接获取第一个即可
        dictionary = front.getTheDict();
        pageInfo = front.getThePageInfo();

        // 拼接内容
        content = "";
        for (int i = 1; i < l.size() - 1; i++)
        {
            if (i != 1) content += "\n\n";
            content += l.get(i).getContent();
        }
        content = TextPinyinIPA.transferPinyin(content, Dialect.NAM, true);
    }

    public static RefDraft of(List<RefEntity> list)
    {
        return new RefDraft(list);
    }

    /**
     * 转换成数据库实体类
     *
     * @return <br> left：列表，页面边界标记，0为开始 1为结束
     * <br> right：列表，页面内容部分
     */
    public Pair<Twin<RefEntity>, List<RefEntity>> transfer()
    {
        // 页面开始和结束部分，页数的边界部分
        var edge = Twin.of(
                new RefEntity(dictionary, frontSort, Keyword.FRONT_OF_PAGE, pageInfo),
                new RefEntity(dictionary, endSort, Keyword.END_OF_PAGE, pageInfo)
        );
        edge.handle(i -> i.setLocked(false));

        // 中段，内容部分
        var mid = new ArrayList<RefEntity>();
        var texts = TextPinyinIPA.transferPinyin(content, Dialect.NAM, false).split("\n\n");
        var sorts = FractionIndex.between(frontSort, endSort, texts.length);
        for (int i = 0; i < sorts.size(); i++)
            mid.add(new RefEntity(dictionary, sorts.get(i), texts[i], pageInfo));

        ListTool.handle(mid, i -> i.setLocked(false));

        return Pair.of(edge, mid);
    }
}
