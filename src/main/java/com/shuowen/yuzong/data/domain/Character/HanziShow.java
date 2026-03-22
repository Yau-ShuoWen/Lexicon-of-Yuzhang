package com.shuowen.yuzong.data.domain.Character;

import com.shuowen.yuzong.Linguistics.Scheme.SPinyin;
import com.shuowen.yuzong.Linguistics.Scheme.RPinyin;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.SetTool;
import com.shuowen.yuzong.Tool.RichTextUtil;
import com.shuowen.yuzong.Tool.dataStructure.UChar;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Twin;
import com.shuowen.yuzong.data.domain.IPA.*;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinFormatter;
import com.shuowen.yuzong.data.domain.Reference.DictCode;
import lombok.Data;

import java.util.*;
import java.util.function.Function;

/**
 * 用作词条的汉字，隐藏细节
 */
@Data
public class HanziShow
{
    private final UChar hanzi;
    private final List<Info> info;

    @Data
    public static class Info
    {
        RPinyin mainPy;
        UString special;
        List<Pair<UString, RPinyin>> variantPy;
        Set<UChar> similar;
        List<String> mdrInfo;
        List<Pair<String, String>> ipa;
        List<UString> mean;
        List<Twin<UString>> note;
        //TODO:refer!
    }

    public static HanziShow of(HanziGroup hz, final IPAData ipa)
    {
        return new HanziShow(hz.getData(), ipa);
    }

    private HanziShow(List<HanziItem> hz, final IPAData data)
    {
        hanzi = hz.get(0).getHanzi();
        Language l = data.getLanguage();
        Dialect d = data.getDialect();

        @Data
        class tmpInfo
        {
            UniPinyin<?> mainPy;                                           // 标准拼音
            Set<Integer> special = new TreeSet<>();                       // 特殊性数字：默认顺序的集合
            Set<Pair<UString, UniPinyin<?>>> variantPy = new LinkedHashSet<>();// 读音变体：插入顺序的集合
            Set<UChar> similar = new TreeSet<>();                        // 模糊识别汉字：默认顺序的集合
            List<String> mdrInfo = new ArrayList<>();
            List<Pair<DictCode, UniPinyin<?>>> ipa = new ArrayList<>();
            List<UString> mean = new ArrayList<>();
            List<Twin<UString>> note = new ArrayList<>();
        }

        // 初始化 ----------------------------------------------------------------------

        Map<UniPinyin<?>, tmpInfo> tmpInfoMap = new HashMap<>();

        // 根据信息初始化
        for (HanziItem h : hz)
        {
            // 拼音根据方言的信任初始化创建
            var key = d.trustedCreatePinyin(h.getMainPy());
            // 根据拼音分类：如果没有这个键，就加入，如果加入了这个键，就处理这个键
            tmpInfo info = tmpInfoMap.computeIfAbsent(key, k -> new tmpInfo());

            // 普通类直接变过来
            info.mainPy = key;
            info.special.add(h.getSpecial());
            info.mean.addAll(h.getMean());
            info.mdrInfo.addAll(h.getMdrInfo());
            info.note.addAll(h.getNote());

            // 普通类稍微变动
            info.similar.addAll(SetTool.mapping(h.getSimilar(), i -> i)); // similar直接list转set

            // 拼音根据方言的信任初始化创建
            info.variantPy.addAll(SetTool.mapping(h.getVariantPy(),
                    i -> Pair.of(i.getLeft(), d.trustedCreatePinyin(i.getRight()))
            ));
            info.ipa.addAll(SetTool.mapping(h.getIpa(),
                    i -> Pair.of(new DictCode(i.getLeft()), d.trustedCreatePinyin(SPinyin.of(i.getRight())))
            ));
        }

        // 格式化，两轮循环 ----------------------------------------------------------------------


        // 第一次：获得国际音标资料
        if (data.getPinyinOption().getPhonogram() == Phonogram.PinyinIPA)
        {
            for (var i : tmpInfoMap.values())
                data.add(SetTool.mapping(i.ipa, Pair::getRight));
        }

        // 第二次：对于展示类，格式化拼音、回填国际音标数据、处理字符串内容

        Map<String, Info> infoMap = new TreeMap<>();
        for (var i : tmpInfoMap.values())
        {
            Info info = new Info();

            // 和「数据库拼音初始化」无关的内容先处理
            {
                String tmp = "用法和普通話基本相同。";
                if (i.special.contains(1)) tmp = "方言里有特殊用法。";
                else if (i.special.contains(2)) tmp = "這個漢字沒有考證出本字。";
                else if (i.special.contains(3)) tmp = "方言里不會使用.";

                info.special = ScTcText.get("基本信息概覽：" + tmp, l);
            }

            info.similar = i.similar;
            info.mdrInfo = l.isSimplified() ?
                    ListTool.mapping(i.mdrInfo, MdrTool::showWithPinyin) :
                    ListTool.mapping(i.mdrInfo, MdrTool::showWithZhuyin);

            // 这是三个明确要初始化的内容，已经在上一轮获取了信息
            // 函数：快速调用拼音格式化成字符串
            Function<UniPinyin<?>, RPinyin> format = p -> PinyinFormatter.handle(p, d);
            switch (data.getPinyinOption().getPhonogram())
            {
                case AllPinyin ->
                {
                    info.mainPy = format.apply(i.mainPy);
                    info.variantPy = ListTool.mapping(i.variantPy, pair -> Pair.of(
                            pair.getLeft(), format.apply(pair.getRight())
                    ));
                    info.ipa = ListTool.mapping(i.ipa, pair -> Pair.of(
                            data.getDictionaryName(pair.getLeft()),
                            format.apply(pair.getRight()).toString()
                    ));
                }
                case PinyinIPA ->
                {
                    // mainPy 和 variantPy 和上面的一样
                    info.mainPy = format.apply(i.mainPy);
                    info.variantPy = ListTool.mapping(i.variantPy, pair -> Pair.of(
                            pair.getLeft(), format.apply(pair.getRight())
                    ));
                    // ipa 查询
                    info.ipa = ListTool.mapping(i.ipa, pair -> Pair.of(
                            data.getDictionaryName(pair.getLeft()),
                            data.submitAndGet(pair.getRight(), pair.getLeft()).getValueDirectly("获取音标失败")
                    ));
                }
            }

            // 使用富文本的内容，放在最后，说不定可以用上前面获得的数据
            info.mean = ListTool.mapping(i.mean, s -> RichTextUtil.format(s, d, data));
            info.note = ListTool.mapping(i.note, pair -> Twin.of(pair.getLeft(), RichTextUtil.format(pair.getRight(), d, data)));

            // 提交数据，顺序是权重
            infoMap.put(i.mainPy.getWeight(), info);
        }
        info = new ArrayList<>(infoMap.values());
    }
}
