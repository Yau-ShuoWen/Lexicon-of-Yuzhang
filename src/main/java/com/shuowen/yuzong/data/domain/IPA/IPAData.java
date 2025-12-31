package com.shuowen.yuzong.data.domain.IPA;

import com.shuowen.yuzong.Linguistics.Scheme.Pinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.NullTool;
import com.shuowen.yuzong.Tool.dataStructure.functions.TriFunction;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import lombok.Getter;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BiFunction;

/**
 * 国际音标查询结果集
 *
 * @implNote 使用控制反转查询，主要处理查询内容和结果集
 * @apiNote 为了使得资源复用，所有地方都要使用final标注
 */

public class IPAData
{
    @Getter
    private final Dialect dialect;                         // 方言：预先存进不可变
    @Getter
    private final PinyinOption pinyinOption;               // 拼音格式：预先存进不可变
    private final Map<String, String> dictionary;          // 字典代号-字典中文对照表
    private final Set<Pinyin> buffer = new HashSet<>();    // 没有查的内容的缓冲区
    private final Set<Pinyin> failCase = new HashSet<>();  // 失败用例
    private final Map<Pinyin, Map<String, String>> data = new HashMap<>(); // 信息
    private final TriFunction<Set<Pinyin>, PinyinOption, Dialect, Map<Pinyin, Map<String, String>>> ipaSE;

    public IPAData(Language lang, Dialect d, PinyinOption op,
                   BiFunction<Dialect, Language, Map<String, String>> dictPvd,
                   TriFunction<Set<Pinyin>, PinyinOption, Dialect, Map<Pinyin, Map<String, String>>> ipaPvd)
    {
        dialect = d;
        pinyinOption = op;
        ipaSE = ipaPvd;
        dictionary = dictPvd.apply(d, lang);
    }

    /**
     * 检查是否有历史记录
     */
    private boolean haveHistory(Pinyin pinyin)
    {
        // 被data放进去的是查询成功，被failCase放进去是查询失败
        return data.containsKey(pinyin) || failCase.contains(pinyin);
    }

    /**
     * 检查是否有对应书籍代号
     */
    private boolean inDictionary(String dict)
    {
        return dictionary.containsKey(dict);
    }

    /**
     * 查询函数
     */
    private void search(Set<Pinyin> set)
    {
        data.putAll(ipaSE.apply(set, pinyinOption, dialect));
        for (var i : set) if (!data.containsKey(i)) failCase.add(i);
    }

    /**
     * 使用缓存区信息的查询
     */
    private void search()
    {
        if (buffer.isEmpty()) return;
        search(buffer);
        buffer.clear();
    }

    private void addBuffer(Object input)
    {
        NullTool.checkNotNull(input);
        Pinyin pinyin;

        if (input instanceof String str) pinyin = dialect.createPinyin(str);
        else if (input instanceof Pinyin py) pinyin = py;
        else throw new RuntimeException("内容超出范围");

        if (!haveHistory(pinyin)) buffer.add(pinyin);
    }

    /**
     * 加入函数
     *
     * @param input 可以是数组，可以是Collection，可以是单独的内容
     */
    public void add(Object input)
    {
        NullTool.checkNotNull(input);

        if (input instanceof Collection<?> c)
        {
            for (Object o : c) addBuffer(o);
        }
        else if (input.getClass().isArray())
        {
            for (int i = 0; i < Array.getLength(input); i++)
                addBuffer(Array.get(input, i));
        }
        else addBuffer(input);
    }

    public void add(Object... input)
    {
        NullTool.checkNotNull(input);
        for (Object o : input) add(o);
    }

    /**
     * 通过拼音和字典代号获取内容，适用于批量查询
     *
     * @apiNote 在这之前一定要用这个之前加入信息，这里才可以获得
     */
    public String get(Pinyin pinyin, String dict)
    {
        search(); // 性能仅限于批量查询、批量获取的第一个获取，后续只要不加内容就不会查询
        if (failCase.contains(pinyin)) return pinyin + "找不到对应数据。data not found.";
        if (!inDictionary(dict)) return dict + "找不到对应字典。dictionary not found.";

        return data.get(pinyin).get(dict);
    }

    /**
     * 直接需要内容的时候
     */
    public String getDirectly(Pinyin pinyin, String dict)
    {
        if (!haveHistory(pinyin)) search(Set.of(pinyin));
        return get(pinyin, dict);
    }

    /**
     * 通过拼音和字典代号获取内容，适用于批量查询
     *
     * @apiNote 在这之前一定要用这个之前加入信息，这里才可以获得
     */
    public String get(String py, String dict)
    {
        return get(dialect.createPinyin(py), dict);
    }

    /**
     * 直接需要内容的时候
     */
    public String getDirectly(String py, String dict)
    {
        return getDirectly(dialect.createPinyin(py), dict);
    }

    public String getDictionaryName(String dict)
    {
        if (!inDictionary(dict)) return dict + "找不到对应字典。dictionary not found.";
        return dictionary.get(dict);
    }
}
