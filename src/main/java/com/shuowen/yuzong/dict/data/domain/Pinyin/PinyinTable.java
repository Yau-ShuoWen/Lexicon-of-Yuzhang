package com.shuowen.yuzong.dict.data.domain.Pinyin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.linguistics.util.PinyinCommon;
import com.shuowen.yuzong.util.ext.list.ListTool;
import com.shuowen.yuzong.util.map.KV;
import com.shuowen.yuzong.util.text.ScTcText;
import com.shuowen.yuzong.util.tuple.Pair;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        private Item(String standard, String code)
        {
            this.exist = !"-".equals(standard);
            this.standard = exist ? String.format("[%s]", PinyinCommon.e_A_G(standard)) : "-";
            this.id = exist ? code + "-" + standard : "-";
        }
    }

    // 一组里有最多四个格子，用于最小宽度的换行
    // 需要完整显示，如果剩余控件不够，就整个换行
    @Data
    static class Group
    {
        final List<Item> item = new ArrayList<>();

        public Group(List<String> standards, String code)
        {
            for (String standard : standards) item.add(new Item(standard, code));
        }
    }

    // 一“行”，如果空间不够根据Group会换行，但是一个Line必定换行
    @Data
    static class Line
    {
        final List<Group> group = new ArrayList<>();

        public Line(List<List<String>> data, String code)
        {
            for (List<String> standard : data) group.add(new Group(standard, code));
        }
    }

    // 一个区域，换栏
    @Data
    static class Grid
    {
        String name;     // 区域名称，如：声母，韵母
        String code;
        List<Line> line;

        public Grid(Pair<Map<String, String>, List<List<List<String>>>> data, Language l)
        {
            var gridData = data.getLeft();
            name = ScTcText.get(gridData.get("name-tc"), gridData.get("name-sc"), l);
            code = gridData.get("code");
            line = ListTool.mapping(data.getRight(), i -> new Line(i, code));
        }
    }

    private final List<Grid> table;

    @JsonCreator
    public PinyinTable(Dialect d, Language l)
    {
        var data = readJson(
                KV.get("pinyin-table-display-json:" + d),
                new TypeReference<List<Pair<Map<String, String>, List<List<List<String>>>>>>() {}
        );

        table = ListTool.mapping(data, i -> new Grid(i, l));
    }
}