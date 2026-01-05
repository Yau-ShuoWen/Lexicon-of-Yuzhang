package com.shuowen.yuzong.data.domain.IPA;

import com.shuowen.yuzong.Linguistics.Scheme.Pinyin;
import com.shuowen.yuzong.Tool.dataStructure.functions.QuaFunction;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import lombok.Getter;

import java.util.*;
import java.util.function.BiFunction;

/**
 * 国际音标查询结果集
 *
 * @apiNote 为了使得资源复用，把他当做参数传递的时候都要final标注，这样可以保证局部「单例」，减少重复查询
 */

public class IPAData
{
    @Getter
    private final Language language;
    @Getter
    private final Dialect dialect;                         // 方言：预先存进不可变
    @Getter
    private final PinyinOption pinyinOption;               // 拼音格式：预先存进不可变
    private final Map<String, String> dictionary;          // 字典代号-字典中文对照表
    private final Set<Pinyin> buffer = new HashSet<>();    // 没有查的内容的缓冲区
    private final Set<Pinyin> failCase = new HashSet<>();  // 失败用例
    private final Map<Pinyin, Map<String, String>> data = new HashMap<>(); // 信息
    private final QuaFunction<Set<Pinyin>, PinyinOption, Dialect, Set<String>, Map<Pinyin, Map<String, String>>> ipaSE;

    public IPAData(Language lang, Dialect d, PinyinOption op,
                   BiFunction<Dialect, Language, Map<String, String>> dictPvd,
                   QuaFunction<Set<Pinyin>, PinyinOption, Dialect, Set<String>, Map<Pinyin, Map<String, String>>> ipaPvd)
    {
        language = lang;
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
        data.putAll(ipaSE.apply(set, pinyinOption, dialect, dictionary.keySet()));
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

    /**
     * 加入缓冲区
     */
    private void addBuffer(Pinyin pinyin)
    {
        if (!haveHistory(pinyin)) buffer.add(pinyin);
    }

    /**
     * 单个拼音的增加
     */
    public void add(Pinyin input)
    {
        addBuffer(input);
    }

    /**
     * 数组或者集合拼音的增加
     */
    public void add(Collection<Pinyin> inputs)
    {
        for (var i : inputs) add(i);
    }

    /**
     * 通过拼音和字典代号获取内容，适用于批量查询
     *
     * @apiNote 在这之前一定要用这个之前加入信息，这里才可以获得
     */
    public String submitAndGet(Pinyin pinyin, String dict)
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
        return submitAndGet(pinyin, dict);
    }

    public String getDictionaryName(String dict)
    {
        return inDictionary(dict) ? dictionary.get(dict) :
                "找不到对应字典。dictionary not found.";
    }
}
