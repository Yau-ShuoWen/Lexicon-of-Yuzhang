package com.shuowen.yuzong.linguistics.util;

import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.text.ScTcText;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;

@NoArgsConstructor
@Data
public class PinyinBlock
{
    private String title="";
    private final LinkedHashMap<String, String> map = new LinkedHashMap<>();

    public void add(String key, String value)
    {
        map.put(key, value);
    }

    public void addFirst(String key, String value)
    {
        map.remove(key);
        LinkedHashMap<String, String> newMap = new LinkedHashMap<>();
        newMap.put(key, value);
        newMap.putAll(map);
        map.clear();
        map.putAll(newMap);
    }

    /**
     * 合并。
     * title 用空格连接；
     * 同 key 的 value 用字符串连接；
     * 不同 key 则直接加入。
     */
    public void merge(PinyinBlock other)
    {
        if (other == null) return;

        title = join(title, other.title);

        for (var entry : other.map.entrySet())
        {
            map.merge(entry.getKey(), entry.getValue(), PinyinBlock::join);
        }
    }

    private static String join(String a, String b)
    {
        if (a == null || a.isEmpty()) return b; // 空部分收集数据用的
        if (b == null || b.isEmpty()) return a+" ×"; //加的东西是空的，说明传递无效意义
        return a + " " + b;
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
        for (var entry : map.entrySet())
        {
            s.append(String.format(
                    "│%s┆%s",
                    entry.getKey(),
                    entry.getValue().replace("] [","]  [")
            ));
        }
        String theTitle=title.isEmpty()?"（無效）":title;
        String str = String.format("▕%s%s▏", theTitle.replace("] [","]  ["), s);

        // 调试的时候请开启，否则看不懂字符
        // System.out.println(str.replace("▕", "<").replace("▏", ">").replace("│", "||").replace("┆", "->"));

        return ScTcText.get(str, l).toString();
    }
}