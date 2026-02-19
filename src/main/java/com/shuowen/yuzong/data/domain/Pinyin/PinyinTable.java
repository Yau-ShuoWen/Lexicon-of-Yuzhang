package com.shuowen.yuzong.data.domain.Pinyin;

import com.shuowen.yuzong.Linguistics.Format.PinyinParam;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ObjectTool;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Scheme;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Twin;
import lombok.Data;

import java.util.*;

@Data
public class PinyinTable
{
    private static List<List<String>> split(List<String> strs, String regex)
    {
        if (strs.isEmpty()) throw new RuntimeException();

        List<List<String>> result = new ArrayList<>();
        for (var i : strs) result.add(List.of(i.split(regex)));
        if (!ObjectTool.allEqual(result, List::size))
        {
            System.out.println(result);
            throw new IllegalArgumentException("长度不等");
        }

        int len = result.get(0).size();

        List<List<String>> ans = new ArrayList<>();
        for (int i = 0; i < len; i++) ans.add(new ArrayList<>());
        for (var s : result) for (int j = 0; j < len; j++) ans.get(j).add(s.get(j));

        return ans;
    }

    // 一个格子
    @Data
    static class Item
    {
        final boolean exist;    // 是否存在
        final String standard;  // 标准拼音
        final String keyboard;  // 输入拼音
        final String id;        // 调用地址 initial-b（声母b） last-ang（韵母ang）
        // 其他未来增加

        private Item(String standard, String keyboard, String code)
        {
            this.exist = !"-".equals(standard);
            this.standard = exist ? "[" + standard + "]" : "-";
            this.keyboard = exist ? "[" + keyboard + "]" : "-";
            this.id = exist ? code + "-" + keyboard : "-";
        }
    }

    // 一组里有最多四个格子，用于最小宽度的换行
    // 需要完整显示，如果剩余控件不够，就整个换行
    @Data
    static class Group
    {
        final Twin<String> name;
        final List<Item> item;

        public Group(String nameSc, String nameTc, String standards, String keyboards, String code)
        {
            name = Twin.of(nameSc, nameTc);

            var ans = split(List.of(standards, keyboards), " ");
            item = ListTool.mapping(ans, i -> new Item(i.get(0), i.get(1), code));
        }
    }

    // 一“行”，如果空间不够根据Group会换行，但是一个Line必定换行
    @Data
    static class Line
    {
        final Twin<String> name;       // 行名称：
        final List<Group> group;

        public Line(Map<String, String> data, String code)
        {
            name = Twin.of(data.get("name-sc"), data.get("name-tc"));

            var ans = split(List.of(
                    data.get("subname-sc"), data.get("subname-tc"),
                    data.get("standard"), data.get("keyboard")
            ), "\\|");
            group = ListTool.mapping(ans, i -> new Group(i.get(0).trim(), i.get(1).trim(), i.get(2), i.get(3), code));
        }
    }

    // 一个区域，换栏
    @Data
    static class Grid
    {
        Twin<String> name;     // 区域名称，如：声母，韵母
        String code;
        List<Line> line;

        public Grid(Pair<Map<String, String>, List<Map<String, String>>> data)
        {
            var gridData = data.getLeft();
            name = Twin.of(gridData.get("name-sc"), gridData.get("name-tc"));
            code = gridData.get("code");
            line = ListTool.mapping(data.getRight(), i -> new Line(i, code));
        }
    }

    private final List<Grid> table;

    public PinyinTable(List<Pair<Map<String, String>, List<Map<String, String>>>> data)
    {
        table = ListTool.mapping(data, Grid::new);
    }

    private static Pair<Map<String, String>, List<Map<String, String>>> getTonePreview(Dialect d, String p, Pair<Map<String, String>, List<Map<String, String>>> data)
    {
        List<String> standard = new ArrayList<>();
        List<String> keyboard = new ArrayList<>();

        for (int i = 0; i <= d.getToneAmount(); i++)
        {
            var maybe = d.tryCreatePinyin(p + i);
            if (maybe.isValid())
            {
                var py = maybe.getValue();

                standard.add(PinyinFormatter.handle(py, d, PinyinParam.of(Scheme.STANDARD)));
                keyboard.add(PinyinFormatter.handle(py, d, PinyinParam.of(Scheme.KEYBOARD)));
            }
            else
            {
                standard.add("-");
                keyboard.add("-");
            }
        }
        Map<String, String> preview = data.getRight().get(0);

        preview.put("standard", String.format(preview.get("standard"), standard.toArray()));
        preview.put("keyboard", String.format(preview.get("keyboard"), keyboard.toArray()));
        return data;
    }

    public static PinyinTable getTonePreview(Dialect d, String p)
    {
        Pair<Map<String, String>, List<Map<String, String>>> dataTempate = Pair.of(
                Map.of(
                        "name-sc", "音调",
                        "name-tc", "音調",
                        "code", "tone"
                ),
                new ArrayList<>(List.of(
                        new HashMap<>(Map.of(
                                "name-sc", "",
                                "name-tc", "",
                                "subname-sc", " | ",
                                "subname-tc", " | ",
                                "standard", "%s %s %s %s|%s %s %s %s",
                                "keyboard", "%s %s %s %s|%s %s %s %s"
                        ))
                ))
        );
        return new PinyinTable(List.of(PinyinTable.getTonePreview(d, p, dataTempate)));
    }
}
