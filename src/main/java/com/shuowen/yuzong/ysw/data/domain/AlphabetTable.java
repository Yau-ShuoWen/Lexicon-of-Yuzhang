package com.shuowen.yuzong.ysw.data.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ObjectTool;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.ysw.linguistic.Alphabet;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.service.impl.KV;
import lombok.Data;

import java.util.*;

import static com.shuowen.yuzong.Tool.format.JsonTool.readJson;

@Data
public class AlphabetTable
{
    // 一个格子
    @Data
    static class Item
    {
        final boolean exist;    // 是否存在
        final String standard;  // 标准拼音
        final String id;        // 调用地址 initial-b（声母b） last-ang（韵母ang）

        private Item(String standard, String code, Alphabet d)
        {
            this.exist = !"-".equals(standard);
            this.standard = exist ? (
                    d.getLatin() ? String.format("[%s]", standard) : standard
            ) : "-";
            this.id = exist ? code + "-" + standard : "-";

            // d 暂时不用
        }

    }

    // 一组里有最多四个格子，用于最小宽度的换行
    // 需要完整显示，如果剩余控件不够，就整个换行
    @Data
    static class Group
    {
        final List<Item> item = new ArrayList<>();


        public Group(List<String> standards, String code, Alphabet d)
        {
            int size = ObjectTool.assertEqual(standards.size());
            for (int i = 0; i < size; i++)
                item.add(new Item(standards.get(i), code, d));
        }
    }

    // 一“行”，如果空间不够根据Group会换行，但是一个Line必定换行
    @Data
    static class Line
    {
        final List<Group> group = new ArrayList<>();


        public Line(List<List<String>> data, String code, Alphabet d)
        {
            for (var standard : data)
                group.add(new Group(standard, code, d));
        }
    }

    // 一个区域，换栏
    @Data
    static class Grid
    {
        UString name;     // 区域名称，如：声母，韵母
        String code;
        List<Line> line;


        public Grid(Pair<Map<String, String>, List<List<List<String>>>> data, Alphabet d,Language l)
        {
            var gridData = data.getLeft();
            name = ScTcText.get(gridData.get("name"),l);
            code = gridData.get("code");
            line = ListTool.mapping(data.getRight(), i -> new Line(i, code, d));
        }
    }

    private final List<Grid> table;

    public AlphabetTable(Alphabet d, Language l)
    {
        var data = readJson(
                KV.get("alphabet-table-json:" + d.toString()),
                new TypeReference<List<Pair<Map<String, String>, List<List<List<String>>>>>>() {}
        );
        table = ListTool.mapping(data, i -> new Grid(i, d,l));
    }
}