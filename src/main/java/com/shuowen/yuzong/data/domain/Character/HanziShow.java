package com.shuowen.yuzong.data.domain.Character;

import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.SetTool;
import com.shuowen.yuzong.Tool.RichTextUtil;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.data.domain.IPA.*;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinTool;
import lombok.Data;

import java.util.*;
import java.util.function.Function;

/**
 * 用作词条的汉字，隐藏细节
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
        String mainPy;
        Set<Integer> special;
        List<Pair<String, String>> variantPy;
        Set<String> similar;
        List<String> mdrInfo;
        List<Pair<String, String>> ipa;
        List<String> mean;
        List<Pair<String, String>> note;
        //TODO:refer!
    }

    public static HanziShow of(HanziGroup hz, final IPAData data)
    {
        var list = ListTool.checkSizeOne(hz.getList(), "not found 未找到汉字", "not unique 汉字不唯一");
        return new HanziShow(list, data);
    }

    private HanziShow(List<HanziItem> hz, final IPAData data)
    {
        hanzi = hz.get(0).getHanzi();
        language = data.getLanguage().toString();

        Dialect d = data.getDialect();

        @Data
        class tmpInfo
        {
            UniPinyin<?> mainPy;                                           // 标准拼音
            Set<Integer> special = new TreeSet<>();                       // 特殊性数字：默认顺序的集合
            Set<Pair<String, UniPinyin<?>>> variantPy = new LinkedHashSet<>();// 读音变体：插入顺序的集合
            Set<String> similar = new TreeSet<>();                        // 模糊识别汉字：默认顺序的集合
            List<String> mdrInfo = new ArrayList<>();
            List<Pair<String, UniPinyin<?>>> ipa = new ArrayList<>();
            List<String> mean = new ArrayList<>();
            List<Pair<String, String>> note = new ArrayList<>();
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
            info.mean.addAll(h.getMeanData());
            info.mdrInfo.addAll(h.getMdrInfo());
            info.note.addAll(h.getNoteData());

            // 普通类稍微变动
            info.similar.addAll(SetTool.mapping(h.getSimilarData(), i -> i)); // similar直接list转set

            // 拼音根据方言的信任初始化创建
            info.variantPy.addAll(SetTool.mapping(h.getMulPyData(),
                    i -> Pair.of(i.getLeft(), d.trustedCreatePinyin(i.getRight()))));
            info.ipa.addAll(SetTool.mapping(h.getIpaExpData(),
                    i -> Pair.of(i.getLeft(), d.trustedCreatePinyin(i.getRight()))));
        }

        // 格式化，两轮循环 ----------------------------------------------------------------------


        // 第一次：获得国际音标资料
        for (var i : tmpInfoMap.values())
        {
            switch (data.getPinyinOption().getPhonogram())
            {
                case PinyinIPA -> data.add(SetTool.mapping(i.ipa, Pair::getRight));
                case AllIPA ->
                {
                    data.add(i.mainPy);
                    data.add(SetTool.mapping(i.variantPy, Pair::getRight));
                    data.add(SetTool.mapping(i.ipa, Pair::getRight));
                }
            }
        }

        // 函数：快速调用拼音格式化成字符串
        Function<UniPinyin<?>, String> format = p -> PinyinTool.formatPinyin(p, d);
        String dict = d.getDefaultDict();


        // 第二次：对于展示类，格式化拼音、回填国际音标数据、处理字符串内容
        for (var i : tmpInfoMap.values())
        {
            Info info = new Info();
            // 和「数据库拼音初始化」无关的内容先处理
            info.special = i.special;
            info.similar = i.similar;
            info.mdrInfo = data.getLanguage().isSimplified() ?
                    ListTool.mapping(i.mdrInfo, MdrTool::showWithPinyin) :
                    ListTool.mapping(i.mdrInfo, MdrTool::showWithZhuyin);

            // 这是三个明确要初始化的内容，已经在上一轮获取了信息
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
                            format.apply(pair.getRight())
                    ));
                }
                case PinyinIPA ->
                {
                    // mainPy 和 variantPy 和上面的一样
                    info.mainPy = format.apply(i.mainPy);
                    info.variantPy = ListTool.mapping(i.variantPy, pair -> Pair.of(
                            pair.getLeft(), format.apply(pair.getRight())
                    ));
                    // ipa 和下面一样
                    info.ipa = ListTool.mapping(i.ipa, pair -> Pair.of(
                            data.getDictionaryName(pair.getLeft()),
                            data.submitAndGet(pair.getRight(), pair.getLeft())
                    ));
                }
                case AllIPA ->
                {
                    info.mainPy = data.submitAndGet(i.mainPy, dict);
                    info.variantPy = ListTool.mapping(i.ipa, pair -> Pair.of(
                            pair.getLeft(), data.submitAndGet(pair.getRight(), dict)
                    ));
                    info.ipa = ListTool.mapping(i.ipa, pair -> Pair.of(
                            data.getDictionaryName(pair.getLeft()),
                            data.submitAndGet(pair.getRight(), pair.getLeft())
                    ));
                }
            }

            // 使用富文本的内容，放在最后，说不定可以用上前面获得的数据
            info.mean = ListTool.mapping(i.mean, s -> RichTextUtil.format(s, d, data));
            info.note = ListTool.mapping(i.note, pair -> Pair.of(pair.getLeft(), RichTextUtil.format(pair.getRight(), d, data)));

            // 提交数据，顺序是权重
            infoMap.put(i.mainPy.getWeight(), info);
        }
    }
}
