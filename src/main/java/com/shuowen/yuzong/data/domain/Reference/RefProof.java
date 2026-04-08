package com.shuowen.yuzong.data.domain.Reference;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Tool.FractionIndex;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Twin;
import com.shuowen.yuzong.data.model.Reference.RefEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

import static com.shuowen.yuzong.Tool.format.JsonTool.readJson;
import static com.shuowen.yuzong.Tool.format.JsonTool.toJson;

@Data
@NoArgsConstructor
public class RefProof extends Page
{
    private DictCode dictionary;
    private FractionIndex frontSort;
    private FractionIndex endSort;
    private Pair<String, Integer> pageInfo;

    @Data
    @NoArgsConstructor
    private static class Info
    {
        Integer id;
        UString source;  // 原始数据
        ScTcText text;   // 简体和繁体版本
        ScTcText note;   // 注释

        public Info(RefEntity ck)
        {
            var om = new ObjectMapper();

            this.id = ck.getId();
            source = UString.of(ck.getContent());
            text = readJson(ck.getText(), new TypeReference<>() {}, om);
            note = readJson(ck.getNote(), new TypeReference<>() {}, om);
        }

        public RefEntity transfer(DictCode dict, FractionIndex sort, Pair<String, Integer> pageInfo)
        {
            var om = new ObjectMapper();
            var ans = new RefEntity();
            ans.setId(this.id);
            ans.setTheDict(dict);
            ans.setTheSort(sort);
            ans.setContent(source.toString());
            ans.setThePageInfo(pageInfo);
            ans.setText(toJson(text, om));
            ans.setNote(toJson(note, om));

            return ans;
        }
    }

    private List<Info> contents;

    private RefProof(List<RefEntity> list)
    {
        type = "proof";

        var l = readList(list);
        var front = l.get(0);
        var end = l.get(l.size() - 1);

        // 标记只有sort字段重要
        frontSort = front.getTheSort();
        endSort = end.getTheSort();
        // 已经被证明全部相等，直接获取第一个的对应属性即可
        dictionary = front.getTheDict();
        pageInfo = front.getThePageInfo();

        // 拼接内容
        contents = new ArrayList<>();
        for (int i = 1; i < l.size() - 1; i++)
        {
            contents.add(new Info(l.get(i)));
        }
    }

    public static RefProof of(List<RefEntity> list)
    {
        return new RefProof(list);
    }

    public Pair<Twin<RefEntity>, List<RefEntity>> transfer()
    {
        var edge = Twin.of(
                new RefEntity(dictionary, frontSort, Keyword.FRONT_OF_PAGE, pageInfo),
                new RefEntity(dictionary, endSort, Keyword.END_OF_PAGE, pageInfo)
        );
        edge.handle(i -> i.setLocked(true));

        List<RefEntity> mid = new ArrayList<>();
        var sorts = FractionIndex.between(frontSort, endSort, contents.size());
        for (int i = 0; i < contents.size(); i++)
        {
            mid.add(contents.get(i).transfer(dictionary, sorts.get(i), pageInfo));
        }

        ListTool.handle(mid, i -> i.setLocked(true));

        return Pair.of(edge,mid);
    }
}
