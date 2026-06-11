package com.shuowen.yuzong.data.domain.Character;

import com.shuowen.yuzong.Linguistics.Scheme.RPinyin;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.SetTool;
import com.shuowen.yuzong.Tool.RichTextUtil;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.UChar;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.option.Scheme;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Twin;
import com.shuowen.yuzong.data.domain.IPA.*;
import com.shuowen.yuzong.Linguistics.Scheme.PinyinFormatter;
import com.shuowen.yuzong.data.domain.Reference.RefItem;
import com.shuowen.yuzong.service.impl.Reference.RefReadService;
import lombok.Data;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

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
        Integer special;
        List<Pair<UString, RPinyin>> variantPy;
        Set<UChar> similar;
        List<Pair<String, String>> mdrInfo;
        List<Twin<UString>> note;
    }

    private final LinkedHashSet<RefItem> ref = new LinkedHashSet<>();

    public static HanziShow of(HanziGroup hz, final IPAData data)
    {
        return new HanziShow(hz.getData(), data);
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
            info.mdrInfo.addAll(h.getMdrInfo());
            info.note.addAll(h.getNote());

            // 普通类稍微变动
            info.similar.addAll(SetTool.mapping(h.getSimilar(), i -> i)); // similar直接list转set

            // 拼音根据方言的信任初始化创建
            info.variantPy.addAll(SetTool.mapping(h.getVariantPy(),
                    i -> Pair.of(i.getLeft(), d.trustedCreatePinyin(i.getRight()))
            ));
        }

        // 格式化拼音、回填国际音标数据、处理字符串内容

        Map<String, Info> infoMap = new TreeMap<>();
        for (var i : tmpInfoMap.values())
        {
            Info info = new Info();

            // 和「数据库拼音初始化」无关的内容先处理
            info.special = ((Supplier<Integer>) () ->
            {
                // 优先级别：
                // 1. 有特殊用法的情况下，使用地方少算做特殊用法的一种
                // 2. 使用方法少，就不能说不会使用
                // 3. 没有上面的，才能判断完全不使用
                if (i.special.contains(1)) return 1;
                if (i.special.contains(2)) return 2;
                if (i.special.contains(3)) return 3;
                return 0;
            }).get();

            info.similar = i.similar;
            info.mdrInfo = ListTool.mapping(i.mdrInfo, j -> Pair.of(
                    MdrTool.showWithPinyin(j),
                    MdrTool.showWithZhuyin(j))
            );

            Scheme scheme = data.getPinyinOption().getPinyinMode() == PinyinMode.INTRODUCE ? Scheme.INTRO : Scheme.DISPLAY;

            // 这是三个明确要初始化的内容，已经在上一轮获取了信息
            // 函数：快速调用拼音格式化成字符串
            Function<UniPinyin<?>, RPinyin> format = p -> PinyinFormatter.handle(p, d, scheme);

            info.mainPy = format.apply(i.mainPy);
            info.variantPy = ListTool.mapping(i.variantPy, pair -> Pair.of(pair.getLeft(), format.apply(pair.getRight())));

            info.note = ListTool.mapping(i.note, pair -> Twin.of(pair.getLeft(),
                    RichTextUtil.format(pair.getRight(), data, false, Maybe.nothing(), true)
            ));

            // 提交数据，顺序是权重
            infoMap.put(i.mainPy.getWeight(), info);
        }
        info = new ArrayList<>(infoMap.values());

        ref.addAll(RefReadService.getRef(hz.get(0).getHanzis().toText(), data));
    }
}
