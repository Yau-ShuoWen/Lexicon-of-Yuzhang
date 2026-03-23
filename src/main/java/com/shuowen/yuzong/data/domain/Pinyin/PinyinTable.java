package com.shuowen.yuzong.data.domain.Pinyin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ObjectTool;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.service.impl.KeyValueService;
import lombok.Data;

import java.util.*;

import static com.shuowen.yuzong.Tool.format.JsonTool.readJson;


@Data
public class PinyinTable
{
    // 一个格子
    @Data
    static class Item
    {
        final boolean exist;    // 是否存在
        final String standard;  // 标准拼音
        final String id;        // 调用地址 initial-b（声母b） last-ang（韵母ang）

        private Item(String standard, String keyboard, String code)
        {
            this.exist = !"-".equals(standard);
            this.standard = exist ? String.format("{b [%s]}", standard) : "-";
            this.id = exist ? code + "-" + keyboard : "-";
        }
    }

    // 一组里有最多四个格子，用于最小宽度的换行
    // 需要完整显示，如果剩余控件不够，就整个换行
    @Data
    static class Group
    {
        final List<Item> item = new ArrayList<>();

        public Group(List<String> standards, List<String> keyboards, String code)
        {
            int size = ObjectTool.assertEqual(standards.size(), keyboards.size());
            for (int i = 0; i < size; i++) item.add(new Item(standards.get(i), keyboards.get(i), code));
        }
    }

    // 一“行”，如果空间不够根据Group会换行，但是一个Line必定换行
    @Data
    static class Line
    {
        final List<Group> group = new ArrayList<>();

        public Line(Map<String, List<List<String>>> data, String code)
        {
            var standards = data.get("standard");
            var keyboards = data.get("keyboard");
            int size = ObjectTool.assertEqual(standards.size(), keyboards.size());

            for (int i = 0; i < size; i++)
                group.add(new Group(standards.get(i), keyboards.get(i), code));
        }
    }

    // 一个区域，换栏
    @Data
    static class Grid
    {
        ScTcText name;     // 区域名称，如：声母，韵母
        String code;
        List<Line> line;

        public Grid(Pair<Map<String, String>, List<Map<String, List<List<String>>>>> data)
        {
            var gridData = data.getLeft();
            name = new ScTcText(gridData.get("name-sc"), gridData.get("name-tc"));
            code = gridData.get("code");
            line = ListTool.mapping(data.getRight(), i -> new Line(i, code));
        }
    }

    private final List<Grid> table;

    @JsonCreator
    public PinyinTable(Dialect d)
    {
        var data = readJson(
                KeyValueService.get("pinyin-table-display-json:" + d.toString()),
                new TypeReference<List<Pair<Map<String, String>, List<Map<String, List<List<String>>>>>>>() {},
                new ObjectMapper()
        );

        table = ListTool.mapping(data, Grid::new);
    }
}