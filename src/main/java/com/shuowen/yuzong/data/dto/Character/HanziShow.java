package com.shuowen.yuzong.data.dto.Character;

import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;
import com.shuowen.yuzong.Tool.dataStructure.functions.QuaFunction;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Triple;
import com.shuowen.yuzong.data.domain.Character.HanziEntry;
import com.shuowen.yuzong.data.domain.IPA.IPASyllableStyle;
import com.shuowen.yuzong.data.domain.IPA.IPAToneStyle;
import com.shuowen.yuzong.data.domain.IPA.IPATool;
import com.shuowen.yuzong.data.domain.IPA.Phonogram;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinTool;
import lombok.Data;

import java.util.*;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
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


    @SuppressWarnings ("unchecked")
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


    public static <T extends UniPinyin<U>, U extends PinyinStyle>
    void initPinyinIPA(List<HanziShow> list, U style, Phonogram s, String defaultDict,
                       Function<String, T> pinyinCreator,
                       QuaFunction<Set<T>, IPAToneStyle, IPASyllableStyle, Dialect, Map<T, Map<String, String>>> ipaSE,
                       IPAToneStyle ts, IPASyllableStyle ss,
                       Dialect d)
    {
        Set<String> setToPy = new HashSet<>();
        Set<String> setToIPA = new HashSet<>();
        // 获取内容
        for (var hz : list)
        {
            for (var i : hz.infoMap.values())
            {
                switch (s)
                {
                    case AllPinyin ->
                    {
                        setToPy.add(i.stdPy);
                        for (var j : i.mulPy) setToPy.add(j.getRight());
                        for (var j : i.ipaExp) setToPy.add(j.getRight());
                    }
                    case PinyinIPA ->
                    {
                        setToPy.add(i.stdPy);
                        for (var j : i.mulPy) setToPy.add(j.getRight());
                        for (var j : i.ipaExp) setToIPA.add(j.getRight());
                    }
                    case AllIPA ->
                    {
                        setToIPA.add(i.stdPy);
                        for (var j : i.mulPy) setToIPA.add(j.getRight());
                        for (var j : i.ipaExp) setToIPA.add(j.getRight());
                    }
                }
            }
        }

        Map<String, String> pyData = PinyinTool.formatPinyin(setToPy, pinyinCreator, style);
        Map<String, Map<String, String>> ipaData = IPATool.formatIPA(setToIPA, pinyinCreator, ipaSE, ts, ss, d);

        // 无论是没有这个拼音还是没有这个字典，都直接静默处理
        BiFunction<String, String, String> get = (pinyin, dict) ->
        {
            try
            {
                return ipaData.get(pinyin).get(dict);
            } catch (Exception e)
            {
                return "暂无国际音标，请联系管理员";
            }
        };


        for (var hz : list)
        {
            for (var i : hz.infoMap.values())
            {
                switch (s)
                {
                    case AllPinyin ->
                    {
                        i.stdPy = pyData.get(i.stdPy);
                        for (var j : i.mulPy) j.setRight(pyData.get(j.getRight()));
                        for (var j : i.ipaExp) j.setRight(pyData.get(j.getRight()));
                    }
                    case PinyinIPA ->
                    {
                        i.stdPy = pyData.get(i.stdPy);
                        for (var j : i.mulPy) j.setRight(pyData.get(j.getRight()));
                        for (var j : i.ipaExp) j.setRight(get.apply(j.getRight(), j.getMiddle()));
                    }
                    case AllIPA ->
                    {
                        i.stdPy = get.apply(i.stdPy, defaultDict);
                        for (var j : i.mulPy)
                            j.setRight(get.apply(j.getRight(), defaultDict));
                        for (var j : i.ipaExp) j.setRight(get.apply(j.getRight(), j.getMiddle()));
                    }
                }
            }
        }
    }
}
