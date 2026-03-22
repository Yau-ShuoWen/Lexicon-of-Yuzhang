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

//🗃️ PinyinTable
//1. 硬编码的时候：修改的时候，为什么要重新提交？为什么要重新运行？
//2. 数据库的时候：重构的时候，怎么做简单的备份？
//于是就备份一下，下一个版本注释全删完

//旧版本存档
//[
//  {
//    "left": {
//      "name-sc": "声母",
//      "name-tc": "聲母",
//      "code": "initial"
//    },
//    "right": [
//      {
//        "name-sc": "普通声母",
//        "name-tc": "普通聲母",
//        "subname-sc": "双唇、唇齿音|舌尖、边音|舌根音|舌面前音|舌尖前音",
//        "subname-tc": "雙唇、唇齒音|舌尖、邊音|舌根音|舌面前音|舌尖前音",
//        "standard": "b p m f|d t - l|g k ng h|j q n x|z c s -",
//        "keyboard": "b p m f|d t - l|g k ng h|j q n x|z c s -"
//      }
//    ]
//  },
//  {
//    "left": {
//      "name-sc": "韵母",
//      "name-tc": "韻母",
//      "code": "last"
//    },
//    "right": [
//      {
//        "name-sc": "单韵母",
//        "name-tc": "單韻母",
//        "subname-sc": "普通韵母|介母",
//        "subname-tc": "普通韻母|介母",
//        "standard": "a o e ọ|i u ü -",
//        "keyboard": "a o e oe|i u yu -"
//      },
//      {
//        "name-sc": "复韵母",
//        "name-tc": "複韻母",
//        "subname-sc": "i韵尾|u韵尾",
//        "subname-tc": "i韻尾|u韻尾",
//        "standard": "ai ẹi ui -|au eu ẹu iu",
//        "keyboard": "ai eei ui -|au eu eeu iu"
//      },
//      {
//        "name-sc": "鼻韵母",
//        "name-tc": "鼻韻母",
//        "subname-sc": "前鼻韵母|前鼻韵母|后鼻韵母",
//        "subname-tc": "前鼻韻母|前鼻韻母|後鼻韻母",
//        "standard": "an on en ẹn|in un ün -|ang ong ung -",
//        "keyboard": "an on en een|in un yun -|ang ong ung -"
//      },
//      {
//        "name-sc": "塞韵母",
//        "name-tc": "塞韻母",
//        "subname-sc": "舌尖塞韵母|舌尖塞韵母|舌根塞韵母",
//        "subname-tc": "舌尖塞韻母|舌尖塞韻母|舌根塞韻母",
//        "standard": "at ot et ẹt|it ut üt -|ak ok uk -",
//        "keyboard": "at ot et eet|it ut yut -|ak ok uk -"
//      }
//    ]
//  },
//  {
//    "left": {
//      "name-sc": "特殊音节",
//      "name-tc": "特殊音節",
//      "code": "special"
//    },
//    "right": [
//      {
//        "name-sc": "特殊音节",
//        "name-tc": "特殊音節",
//        "subname-sc": "整体认读|鼻音音节",
//        "subname-tc": "整體認讀|鼻音音節",
//        "standard": "zi ci si -|m n ng -",
//        "keyboard": "zii cii sii -|m n ng -"
//      }
//    ]
//  },
//  {
//    "left": {
//      "name-sc": "音调",
//      "name-tc": "音調",
//      "code": "tone"
//    },
//    "right": [
//      {
//        "name-sc": "",
//        "name-tc": "",
//        "subname-sc": " | ",
//        "subname-tc": " | ",
//        "standard": "a à á ǎ|ā ả a̋ ȁ",
//        "keyboard": "0 1 2 3|4 5 6 7"
//      }
//    ]
//  }
//]

//新版本存档
//[
//  {
//    "left":  {
//      "name-sc": "声母",
//      "name-tc": "聲母",
//      "code":    "initial"
//    },
//    "right": [
//      {
//        "standard": [
//          ["b", "p", "m", "f"], ["d", "t", "-", "l"],
//          ["g", "k", "ng", "h"], ["j", "q", "n", "x"], ["z", "c", "s", "-"]
//        ],
//        "keyboard": [
//          ["b", "p", "m", "f"], ["d", "t", "-", "l"],
//          ["g", "k", "ng", "h"], ["j", "q", "n", "x"], ["z", "c", "s", "-"]
//        ]
//      }
//    ]
//  },
//  {
//    "left":  {
//      "name-sc": "韵母",
//      "name-tc": "韻母",
//      "code":    "last"
//    },
//    "right": [
//      {
//        "standard": [["a", "o", "e", "ọ"], ["i", "u", "ü ", "-"]],
//        "keyboard": [["a", "o", "e", "oe"], ["i", "u", "yu", "-"]]
//      },
//      {
//        "standard": [["ai", "ẹi", "ui", "-"], ["au", "eu", "ẹu", "iu"]],
//        "keyboard": [["ai", "eei", "ui", "-"], ["au", "eu", "eeu", "iu"]]
//      },
//      {
//        "standard": [
//          ["an", "on", "en", "ẹn"], ["an", "on", "en", "ẹn"], ["in", "un", "ün", "-"],
//          ["ang", "ong", "ung", "-"]
//        ],
//        "keyboard": [
//          ["an", "on", "en", "een"], ["an", "on", "en", "een"], ["in", "un", "yun", "-"],
//          ["ang", "ong", "ung", "-"]
//        ]
//      },
//      {
//        "standard": [["at", "ot", "et", "ẹt"], ["it", "ut", "üt ", "-"], ["ak", "ok", "uk", "-"]],
//        "keyboard": [["at", "ot", "et", "eet"], ["it", "ut", "yut", "-"], ["ak", "ok", "uk", "-"]]
//      }
//    ]
//  },
//  {
//    "left":  {
//      "name-sc": "特殊音节",
//      "name-tc": "特殊音節",
//      "code":    "special"
//    },
//    "right": [
//      {
//        "standard": [["zi", "ci", "si", "-"], ["m", "n", "ng", "-"]],
//        "keyboard": [["zii", "cii", "sii", "-"], ["m", "n", "ng", "-"]]
//      }
//    ]
//  },
//  {
//    "left":  {
//      "name-sc": "音调",
//      "name-tc": "音調",
//      "code":    "tone"
//    },
//    "right": [
//      {
//        "standard": [["a", "à", "á", "ǎ"], ["ā", "ả", "a̋", "ȁ"]],
//        "keyboard": [["0", "1", "2", "3"], ["4", "5", "6", "7"]]
//      }
//    ]
//  }
//]

@Data
public class PinyinTable
{
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
            this.standard = exist ? String.format("{b [%s]}", standard) : "-";
            this.keyboard = exist ? String.format("{b [%s]}", keyboard) : "-";
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