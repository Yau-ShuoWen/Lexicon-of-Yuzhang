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
    private final LinkedHashMap<String, String> tags = new LinkedHashMap<>();

    public void add(String key, String value)
    {
        map.put(key, value);
    }

    public void add(String tag, String key, String value)
    {
        map.put(key, value);
        tags.put(key, tag);
    }

    public void addFirst(String key, String value)
    {
        map.remove(key);
        tags.remove(key);
        LinkedHashMap<String, String> newMap = new LinkedHashMap<>();
        newMap.put(key, value);
        newMap.putAll(map);
        map.clear();
        map.putAll(newMap);
    }

    public void addFirst(String tag, String key, String value)
    {
        addFirst(key, value);
        LinkedHashMap<String, String> newTags = new LinkedHashMap<>();
        newTags.put(key, tag);
        newTags.putAll(tags);
        tags.clear();
        tags.putAll(newTags);
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
            var tag = other.tags.get(entry.getKey());
            if (tag != null) tags.put(entry.getKey(), tag);
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
            String tag = tags.get(entry.getKey());
            String value = entry.getValue().replace("] [","]  [");
            if (tag == null || tag.isEmpty()) s.append(String.format("│%s┆%s", entry.getKey(), value));
            else s.append(String.format("│%s┆%s┆%s", tag, entry.getKey(), value));
        }
        String theTitle=title.isEmpty()?"（無效）":title;
        String str = String.format("▕%s%s▏", theTitle.replace("] [","]  ["), s);

        // 调试的时候请开启，否则看不懂字符
        // System.out.println(str.replace("▕", "<").replace("▏", ">").replace("│", "||").replace("┆", "->"));

        return ScTcText.get(str, l).toString();
    }
}
// 读起来想普通话的 <putonghua>
// 标准写法 <standard>
// 键盘输入 <keyboard>
// 本词典标准IPA <yzcIPA>
// 其他版本拼音 <other>
// 词语对应版本IPA <dictIPA>
