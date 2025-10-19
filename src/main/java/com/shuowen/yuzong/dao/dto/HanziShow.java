package com.shuowen.yuzong.dao.dto;

import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;
import com.shuowen.yuzong.Tool.dataStructure.Language;
import com.shuowen.yuzong.Tool.dataStructure.Status;
import com.shuowen.yuzong.Tool.dataStructure.Triple;
import com.shuowen.yuzong.dao.domain.Character.HanziEntry;
import com.shuowen.yuzong.dao.domain.IPA.IPAToneStyle;
import com.shuowen.yuzong.dao.domain.Pinyin.PinyinTool;
import lombok.Data;
import com.shuowen.yuzong.Tool.dataStructure.Pair;

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
        List<String> pyExplain = new ArrayList<>();
        List<String> mean = new ArrayList<>();
        List<String> note = new ArrayList<>();
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

            // true 表示获取的不是普通的值而是专门为了展示简化的值
            info.mulPy.addAll(data.getMulPyPair());
            info.ipaExp.addAll(data.getIpaExpTriple());
            info.pyExplain.addAll(data.getPyExplainText());
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
    void initPinyinIPA(List<HanziShow> list, U style, Status s, String defaultDict,
                       Function<String, T> creator,
                       BiFunction<Set<T>, IPAToneStyle, Map<T, Map<String, String>>> ipaSE,
                       IPAToneStyle ms)
    {
        Set<String> usePy = new HashSet<>();
        Set<String> useIPA = new HashSet<>();
        // 获取内容
        for (var hz : list)
        {
            for (var i : hz.infoMap.values())
            {
                switch (s)
                {
                    case AllPinyin ->
                    {
                        usePy.add(i.stdPy);
                        for (var j : i.mulPy) usePy.add(j.getRight());
                        for (var j : i.ipaExp) usePy.add(j.getRight());
                    }
                    case PinyinIPA ->
                    {
                        usePy.add(i.stdPy);
                        for (var j : i.mulPy) usePy.add(j.getRight());
                        for (var j : i.ipaExp) useIPA.add(j.getRight());
                    }
                    case AllIPA ->
                    {
                        useIPA.add(i.stdPy);
                        for (var j : i.mulPy) useIPA.add(j.getRight());
                        for (var j : i.ipaExp) useIPA.add(j.getRight());
                    }
                }
            }
        }

        Map<String, String> pyData = PinyinTool.formatPinyin(usePy, style, creator);
        Map<String, Map<String, String>> ipaData = PinyinTool.formatIPA(useIPA, creator, ipaSE, ms);

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
                        for (var j : i.ipaExp) j.setRight(ipaData.get(j.getRight()).get(j.getMiddle()));
                    }
                    case AllIPA ->
                    {
                        i.stdPy = ipaData.get(i.stdPy).getOrDefault(defaultDict, "国际音标无效");
                        for (var j : i.mulPy)
                            j.setRight(ipaData.get(j.getRight()).getOrDefault(defaultDict, "国际音标无效"));
                        for (var j : i.ipaExp) j.setRight(ipaData.get(j.getRight()).get(j.getMiddle()));
                    }
                }
            }
        }
    }
}
