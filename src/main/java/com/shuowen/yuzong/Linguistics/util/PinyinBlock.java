package com.shuowen.yuzong.Linguistics.util;

import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.util.text.ScTcText;
import com.shuowen.yuzong.util.tuple.Pair;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;

@NoArgsConstructor
@Data
public class PinyinBlock
{
    private String title;
    private final LinkedList<Pair<String, String>> list = new LinkedList<>();

    public void add(String key, String value)
    {
        list.add(Pair.of(key, value));
    }

    public void addFirst(String key, String value)
    {
        list.addFirst(Pair.of(key, value));
    }

    /**
     * 格式：
     * <codeblock>▕折叠口│标题1┆内容1│标题2┆内容2│富文本可增减……▏</codeblock>
     * <br>
     * 都是特殊字符（制表符），debug的时候请不要输入，要依赖复制。
     */
    public String toTheString(Language l)
    {
        StringBuilder s = new StringBuilder();
        for (var i : list)
        {
            s.append(String.format("│%s┆%s", i.getLeft(), i.getRight()));
        }
        String str = String.format("▕%s%s▏", title, s);

        // 调试的时候请开启，否则看不懂字符
        // System.out.println(str.replace("▕", "<").replace("▏", ">").replace("│", "||").replace("┆", "->"));

        return ScTcText.get(str, l).toString();
    }
}