package com.shuowen.yuzong.data.dto.Character;

import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;
import com.shuowen.yuzong.Tool.dataStructure.functions.TriFunction;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Triple;
import com.shuowen.yuzong.data.domain.Character.HanziEntry;
import com.shuowen.yuzong.data.domain.IPA.*;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinTool;
import lombok.Data;

import java.util.*;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * 用作词条的汉字，隐藏细节，不显示简繁体等信息
 */
@Data
public class HanziShow
{
    protected String hanzi;
    protected String language;
    protected Map<String, Info> infoMap = new HashMap<>();

    @Data
    static class Info
    {
        String stdPy;
        boolean special;
        List<Pair<String, String>> mulPy = new ArrayList<>();
        List<Triple<String, String, String>> ipaExp = new ArrayList<>();
        List<String> mean = new ArrayList<>();
        List<Pair<String, String>> note = new ArrayList<>();
        //TODO:refer!
    }


    public HanziShow(HanziEntry hz)
    {
        /* 目前暂时这么认为：
         * Language修改的地方只有add()函数里，说明只有经过合并的，并且明确是简体或者
         * 繁体的内容才允许进入HanziShow阶段，否则可能是刚从数据库里拿出来的数据等，
         * 通过了这个检查就认为是split了的，可以默认数组里汉字相同、已经转简繁等
         */
        if (hz == null || hz.getLanguage() == Language.CH || hz.isEmpty())
            throw new NoSuchElementException("还未初始化好");

        hanzi = (hz.getLanguage() == Language.SC) ?
                hz.getItem(0).getHanzi() : hz.getItem(0).getHantz();
        language = hz.getLanguage().toString();


        for (int i = 0; i < hz.getList().size(); i++)
        {
            var data = hz.getItem(i);
            var pinyin = data.getStdPy();

            Info info = infoMap.computeIfAbsent(pinyin, k -> new Info());
            info.stdPy = pinyin;


            /* 只要编号不是0，都可以认为是特殊用法
             * 对于同一个读音的内容，只要有一个特殊，都算做特殊
             * */
            info.special = (info.special || data.getSpecial() != 0);

            info.mulPy.addAll(data.getMulPyPair());
            info.ipaExp.addAll(data.getIpaExpTriple());
            info.mean.addAll(data.getMeanText());
            info.note.addAll(data.getNoteText());
        }
    }

    public static HanziShow of(HanziEntry hz)
    {
        return new HanziShow(hz);
    }

    public static List<HanziShow> ListOf(List<HanziEntry> hz)
    {
        List<HanziShow> ans = new ArrayList<>();
        for (var i : hz) ans.add(of(i));
        return ans;
    }


    public <T extends UniPinyin<U>, U extends PinyinStyle>
    void init(U style, PinyinOption op, Dialect d,
              TriFunction<Set<T>, PinyinOption, Dialect, Map<T, Map<String, String>>> ipaSE
    )
    {
        Function<String, T> pinyinFactory = d.getFactory();
        Function<String, String> format = p -> PinyinTool.formatPinyin(p, pinyinFactory, style);
        String dict=d.getDefaultDict();

        IPAData data = new IPAData();

        // 格式化拼音
        for (var i : infoMap.values())
        {
            switch (op.getPhonogram())
            {
                case AllPinyin ->
                {
                    i.stdPy = format.apply(i.stdPy);
                    for (var j : i.mulPy) j.setRight(format.apply(j.getRight()));
                    for (var j : i.ipaExp) j.setRight(format.apply(j.getRight()));
                }
                case PinyinIPA ->
                {
                    i.stdPy = format.apply(i.stdPy);
                    for (var j : i.mulPy) j.setRight(format.apply(j.getRight()));
                }
            }
        }

        // 获得国际音标资料
        for (var i : infoMap.values())
        {
            switch (op.getPhonogram())
            {
                case PinyinIPA ->
                {
                    for (var j : i.ipaExp) data.add(j.getRight());
                }
                case AllIPA ->
                {
                    data.add(i.stdPy);
                    for (var j : i.mulPy) data.add(j.getRight());
                    for (var j : i.ipaExp) data.add(j.getRight());
                }
            }
        }

        data.search(d, op, ipaSE); // 提交查询

        // 回填
        for (var i : infoMap.values())
        {
            switch (op.getPhonogram())
            {
                case PinyinIPA ->
                {
                    for (var j : i.ipaExp) j.setRight(data.get(j.getRight(), j.getMiddle()));
                }
                case AllIPA ->
                {
                    i.stdPy = data.get(i.stdPy, dict);
                    for (var j : i.mulPy) j.setRight(data.get(j.getRight(), dict));
                    for (var j : i.ipaExp) j.setRight(data.get(j.getRight(), j.getMiddle()));
                }
            }
        }
    }
}
