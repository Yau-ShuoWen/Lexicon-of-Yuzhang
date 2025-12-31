package com.shuowen.yuzong.data.dto.Character;

import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.RichTextUtil;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.data.domain.Character.Hanzi;
import com.shuowen.yuzong.data.domain.Character.HanziEntry;
import com.shuowen.yuzong.data.domain.Character.MdrTool;
import com.shuowen.yuzong.data.domain.IPA.*;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinTool;
import lombok.Data;

import java.util.*;
import java.util.function.Function;

/**
 * 用作词条的汉字，隐藏细节，不显示简繁体等信息
 */
@Data
public class HanziShow
{
    protected String hanzi;
    protected String language;
    protected Map<String, Info> infoMap = new TreeMap<>();

    @Data
    public static class Info
    {
        String stdPy;
        List<Integer> special = new ArrayList<>();
        List<Pair<String, String>> mulPy = new ArrayList<>();
        List<String> similar = new ArrayList<>();
        List<String> mdrInfo = new ArrayList<>();
        List<Pair<String, String>> ipaExp = new ArrayList<>();
        List<String> mean = new ArrayList<>();
        List<Pair<String, String>> note = new ArrayList<>();
        //TODO:refer!
    }


    private HanziShow(List<Hanzi> hz, Language language)
    {
        hanzi = (language == Language.SC) ? hz.get(0).getHanzi() : hz.get(0).getHantz();
        this.language = language.toString();

        for (Hanzi data : hz)
        {
            String pinyin = data.getStdPy();
            // 根据拼音分类
            // 如果没有这个键，就加入，如果加入了这个键，就处理这个键
            Info info = infoMap.computeIfAbsent(pinyin, k -> new Info());

            info.stdPy = pinyin;
            info.special.add(data.getSpecial());
            info.similar.addAll(data.getSimilarData());
            info.mulPy.addAll(data.getMulPyData());
            info.mdrInfo.addAll(data.getMdrInfo());
            info.ipaExp.addAll(data.getIpaExpData());
            info.mean.addAll(data.getMeanData());
            info.note.addAll(data.getNoteData());
        }

        for (var i : infoMap.values())
        {
            // 保留原来顺序的去重
            i.mulPy = new ArrayList<>(new LinkedHashSet<>(i.mulPy));
            // 有顺序的去重
            i.special = new ArrayList<>(new TreeSet<>(i.special));
            i.similar = new ArrayList<>(new TreeSet<>(i.similar));
        }
    }

    public static List<HanziShow> listOf(HanziEntry hz)
    {
        List<HanziShow> ans = new ArrayList<>();
        for (var i : hz.getList()) ans.add(new HanziShow(i, hz.getLanguage()));
        return ans;
    }

    public void init(Dialect d, PinyinOption op, final IPAData data)
    {
        Function<String, String> format = p -> PinyinTool.formatPinyin(p, d);
        String dict = d.getDefaultDict();

        // 两轮循环
        // 第一次：格式化拼音、获得国际音标资料、处理字符串内容
        // 第二次：回填国际音标数据

        for (var i : infoMap.values())
        {
            // 格式化拼音
            switch (op.getPhonogram())
            {
                case AllPinyin ->
                {
                    i.stdPy = format.apply(i.stdPy);
                    for (var j : i.mulPy) j.setRight(format.apply(j.getRight()));
                    for (var j : i.ipaExp)
                    {
                        j.setLeft(data.getDictionaryName(j.getLeft()));
                        j.setRight(format.apply(j.getRight()));
                    }
                }
                case PinyinIPA ->
                {
                    i.stdPy = format.apply(i.stdPy);
                    for (var j : i.mulPy) j.setRight(format.apply(j.getRight()));
                }
            }

            // 获得国际音标资料
            switch (op.getPhonogram())
            {
                case PinyinIPA -> data.add(ListTool.mapping(i.ipaExp, Pair::getRight));
                case AllIPA -> data.add(i.stdPy,
                        ListTool.mapping(i.mulPy, Pair::getRight),
                        ListTool.mapping(i.ipaExp, Pair::getRight)
                );
            }

            // 处理字符串中内容
            i.mdrInfo.replaceAll(MdrTool::format);
            i.mean.replaceAll(s -> RichTextUtil.format(s, d, data));
            for (var j : i.note) j.setRight(RichTextUtil.format(j.getRight(), d, data));
        }

        // 回填
        for (var i : infoMap.values())
        {
            switch (op.getPhonogram())
            {
                case PinyinIPA ->
                {
                    for (var j : i.ipaExp)
                    {
                        // 先把拼音转换了，再把简写的转换了，因为前者用到了词典简写
                        j.setRight(data.get(j.getRight(), j.getLeft()));
                        j.setLeft(data.getDictionaryName(j.getLeft()));
                    }
                }
                case AllIPA ->
                {
                    i.stdPy = data.get(i.stdPy, dict);
                    for (var j : i.mulPy) j.setRight(data.get(j.getRight(), dict));
                    for (var j : i.ipaExp) j.setRight(data.get(j.getRight(), j.getLeft()));
                }
            }
        }
    }
}
