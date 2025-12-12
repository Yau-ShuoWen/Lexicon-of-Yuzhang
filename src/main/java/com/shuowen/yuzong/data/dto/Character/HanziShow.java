package com.shuowen.yuzong.data.dto.Character;

import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;
import com.shuowen.yuzong.Tool.RichTextUtil;
import com.shuowen.yuzong.Tool.dataStructure.functions.TriFunction;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.data.domain.Character.HanziEntry;
import com.shuowen.yuzong.data.domain.Character.MdrTool;
import com.shuowen.yuzong.data.domain.IPA.*;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinTool;
import lombok.Data;

import java.util.*;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static com.shuowen.yuzong.Tool.JavaUtilExtend.MapTool.getOrDefault;

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
        List<Integer> special = new ArrayList<>();
        List<Pair<String, String>> mulPy = new ArrayList<>();
        List<String> similar = new ArrayList<>();
        List<String> mdrInfo = new ArrayList<>();
        List<Pair<String, String>> ipaExp = new ArrayList<>();
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

            // 根据拼音分类
            // 如果没有这个键，就加入，如果加入了这个键，就处理这个键
            Info info = infoMap.computeIfAbsent(pinyin, k -> new Info());
            info.stdPy = pinyin;

            info.special.add(data.getSpecial());     // 特殊性直接加入
            info.similar.addAll(data.getSimilarData());
            info.mulPy.addAll(data.getMulPyData());
            info.mdrInfo.addAll(data.getMdrInfo());
            info.ipaExp.addAll(data.getIpaExpData());
            info.mean.addAll(data.getMeanData());
            info.note.addAll(data.getNoteData());
        }

        for (var i : infoMap.values())
        {
            // 有顺序的去重
            i.special = new ArrayList<>(new TreeSet<>(i.special));
            i.similar = new ArrayList<>(new TreeSet<>(i.similar));
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
    void init(U style, PinyinOption op, Dialect d, Map<String, String> dictInfo,
              TriFunction<Set<T>, PinyinOption, Dialect, Map<T, Map<String, String>>> ipaSE
    )
    {
        Function<String, T> pinyinFactory = d.getFactory();
        Function<String, String> format = p -> PinyinTool.formatPinyin(p, pinyinFactory, style);
        String dict = d.getDefaultDict();

        IPAData data = new IPAData();

        // 三轮循环
        // 第一次：格式化拼音、获得国际音标资料、处理字符串内容
        // 第二次：回填国际音标数据
        // 第三次：修改字段名称

        for (var i : infoMap.values())
        {
            // 格式化拼音
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

            // 获得国际音标资料
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

            // 处理字符串中内容
            i.mdrInfo.replaceAll(MdrTool::format);
            i.mean.replaceAll(s -> RichTextUtil.format(s, d));
            for (var j : i.note) j.setRight(RichTextUtil.format(j.getRight(), d));
        }

        data.search(d, op, ipaSE); // 提交查询

        // 回填
        for (var i : infoMap.values())
        {
            switch (op.getPhonogram())
            {
                case PinyinIPA ->
                {
                    for (var j : i.ipaExp) j.setRight(data.get(j.getRight(), j.getLeft()));
                }
                case AllIPA ->
                {
                    i.stdPy = data.get(i.stdPy, dict);
                    for (var j : i.mulPy) j.setRight(data.get(j.getRight(), dict));
                    for (var j : i.ipaExp) j.setRight(data.get(j.getRight(), j.getLeft()));
                }
            }
        }

        // 这个不能合并到前面，因为在格式化的时候用到了词典简写，现在才能换掉
        for (var i : infoMap.values())
        {
            for (var j : i.ipaExp)
                j.setLeft(getOrDefault(dictInfo, j.getLeft(), s -> ("《" + s + "》"), "词典错误"));
        }
    }
}
