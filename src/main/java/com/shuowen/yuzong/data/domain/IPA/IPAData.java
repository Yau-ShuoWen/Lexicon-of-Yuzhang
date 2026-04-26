package com.shuowen.yuzong.data.domain.IPA;

import com.shuowen.yuzong.Linguistics.IPA.IPinyin;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.data.domain.Reference.DictCode;
import com.shuowen.yuzong.data.domain.Reference.DictGroup;
import com.shuowen.yuzong.service.impl.IPA.IPAService;
import com.shuowen.yuzong.service.impl.Reference.DictService;
import lombok.Getter;

import java.util.*;

/**
 * 国际音标查询结果集
 *
 * @apiNote 把他当做参数传递的时候都要final标注，这样可以保证共用同一个实例，减少重复查询
 */
public class IPAData
{
    @Getter
    private final Language language;
    @Getter
    private final Dialect dialect;                         // 方言：预先存进不可变
    @Getter
    private final PinyinOption pinyinOption;               // 拼音格式：预先存进不可变
    private final DictGroup dictionary;          // 字典代号-字典中文对照表
    private final Set<IPinyin> buffer = new HashSet<>();    // 没有查的内容的缓冲区
    private final Set<IPinyin> failCase = new HashSet<>();  // 失败用例
    private final Map<IPinyin, Map<DictCode, String>> data = new HashMap<>(); // 信息

    public IPAData(Language l, Dialect d, PinyinOption op)
    {
        language = l;
        dialect = d;
        pinyinOption = op;
        dictionary = DictService.getDictionary(dialect, language);
    }

    /**
     * 检查拼音是否有历史记录
     */
    private boolean haveHistory(IPinyin pinyin)
    {
        // 被data放进去的是查询成功，被failCase放进去是查询失败
        return data.containsKey(pinyin) || failCase.contains(pinyin);
    }

    /**
     * 检查是否有对应书籍代号
     */
    private boolean inDictionary(DictCode dict)
    {
        return dictionary.containDict(dict);
    }

    /**
     * 查询函数
     */
    private void search(Set<IPinyin> set)
    {
        data.putAll(IPAService.getTheIPA(set, pinyinOption, dialect, dictionary.getKeySet()));
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
    private void addBuffer(IPinyin pinyin)
    {
        if (!haveHistory(pinyin)) buffer.add(pinyin);
    }

    /**
     * 单个拼音的增加
     */
    public void add(IPinyin input)
    {
        addBuffer(input);
    }

    /**
     * 数组或者集合拼音的增加
     */
    public void add(Collection<IPinyin> inputs)
    {
        for (var i : inputs) add(i);
    }

    /**
     * 通过拼音和字典代号获取内容，适用于批量查询
     *
     * @apiNote 在这之前一定要用这个之前加入信息，这里才可以获得
     */
    public Maybe<String> submitAndGet(IPinyin pinyin, DictCode dict)
    {
        search(); // 性能仅限于批量查询、批量获取的第一个获取，后续只要不加内容就不会查询

        if (!inDictionary(dict)) return Maybe.nothing();
        if (failCase.contains(pinyin)) return Maybe.nothing();
        if (!data.get(pinyin).containsKey(dict)) return Maybe.nothing();
        return Maybe.exist(data.get(pinyin).get(dict));
    }

    /**
     * 直接需要内容的时候
     */
    public Maybe<String> getDirectly(IPinyin pinyin, DictCode dict)
    {
        if (!haveHistory(pinyin)) search(Set.of(pinyin));
        return submitAndGet(pinyin, dict);
    }

    public String getDictionaryName(DictCode dict)
    {
        return inDictionary(dict) ?
                dictionary.getName(dict, language) :
                "找不到对应字典。dictionary not found.";
    }
}
