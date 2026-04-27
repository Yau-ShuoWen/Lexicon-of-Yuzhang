package com.shuowen.yuzong.Linguistics.Mandarin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.py.Pinyin;
import com.hankcs.hanlp.dictionary.py.PinyinDictionary;
import com.shuowen.yuzong.Linguistics.Scheme.RPinyin;
import com.shuowen.yuzong.Linguistics.Scheme.SPinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.error.InvalidPinyinException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.*;

/**
 * 所有汉语拼音有关内容都放在这里， Pinyin类不要用在外面，外面直接用字符串接住，
 * 改掉了一些不想要的形式，比如 {@code none5} 的写法和  {@code 5} 作为轻声，
 * 顺便为两个常用字补丁一个读音。
 */
@Getter
@ToString
@EqualsAndHashCode
public class HanPinyin
{
    private final SPinyin split;
    @JsonValue
    private final RPinyin read;

    private HanPinyin(String syll, Maybe<String> tone)
    {
        split = SPinyin.of(syll, tone);
        read = topMark();
    }

    public String getSyll()
    {
        return split.getSyll();
    }

    // 直接返回，因为没有音调和轻声的表现形式是一样的。
    public int getTone()
    {
        return split.getTone().handleIfExist(Integer::valueOf).getValueOrDefault(0);
    }

    /**
     * 根据Hanlp的拼音，私有函数，因为外部拿不到hanlp的拼音
     */
    private static Maybe<HanPinyin> tryOf(Pinyin p)
    {
        try
        {
            var syll = p.getPinyinWithoutTone();
            if ("none".equals(syll)) return Maybe.nothing();

            var tone = p.getTone();
            if (tone == 5) tone = 0;  //HanLP使用5表示轻声

            return Maybe.exist(new HanPinyin(syll, Maybe.exist(String.valueOf(tone))));
        } catch (InvalidPinyinException e)
        {
            return Maybe.nothing();
        }
    }

    /**
     * 搞半天还要自己拆
     */
    @JsonCreator
    public static HanPinyin of(String p)
    {
        var pinyin = SPinyin.of(p);
        return new HanPinyin(pinyin.getSyll(), pinyin.getTone());
    }

    /**
     * 字转拼音数组：一段这样的文字，自動判斷多音字 -> [yi1, duan4, zhe4, yang4, de5, wen2, zi4]
     */
    public static List<Maybe<HanPinyin>> textPinyin(String text)
    {
        List<Pinyin> pinyin = HanLP.convertToPinyinList(text);
        List<Maybe<HanPinyin>> ans = new ArrayList<>();
        if (pinyin.size() != text.length()) throw new RuntimeException("数量不对应");
        for (int i = 0; i < pinyin.size(); i++)
        {
            ans.add(switch (text.charAt(i))
                    {
                        case '兀' -> Maybe.exist(of("wu4"));
                        case '嗀' -> Maybe.exist(of("hu4"));
                        default -> tryOf(pinyin.get(i));
                    }
            );
        }
        return ans;
    }

    /**
     * 字转拼音数组：行 ->[xing2, hang2]
     */
    public static List<HanPinyin> toPinyinList(String c)//看上去是字符串，但只调用一个字
    {
        // hanlp的小bug，读音标不出来
        if ("兀".equals(c)) return new ArrayList<>(List.of(of("wu4")));
        if ("嗀".equals(c)) return new ArrayList<>(List.of(of("hu4"), of("gu3")));

        Pinyin[] py = PinyinDictionary.get(c);

        return (py == null) ? new ArrayList<>() :
                ListTool.mapping(Arrays.asList(py), i -> HanPinyin.tryOf(i).getValue());
    }


    /**
     * 把使用数字标注音调的读音，把{@code pin1} 转换成{@code pīn}，没有安全检查，
     * <p>
     * 使用的算法是基于简化描述：<br>
     * 1. 依照 {@code a > o > e > i > u > ü} 的顺序标记在最先出现的字母上<br>
     * 2. 例外的只有当出现 {@code iu/ui}组合时标记在后一个字母上（而{@code ui}也符合{@code i > u}，所以不用单独拿出来）
     *
     * @return 无效的时候使用空字符串做返回值
     **/
    private RPinyin topMark()
    {
        var syll = getSyll().replace("v", "ü");
        var tone = getTone();

        // 二维查找：map.get(元音)[声调]
        Map<String, String[]> map = Map.of(
                "a", "aāáǎà".split(""),
                "o", "oōóǒò".split(""),
                "e", "eēéěè".split(""),
                "i", "iīíǐì".split(""),
                "u", "uūúǔù".split(""),
                "ü", "üǖǘǚǜ".split("")
        );

        // iu是特殊规则的例外，改变后直接返回
        if (syll.contains("iu")) return RPinyin.of(syll.replace("u", map.get("u")[tone]));
        // 通常情况按照顺序识别
        for (String i : "aoeiuü".split(""))
            if (syll.contains(i)) return RPinyin.of(syll.replace(i, map.get(i)[tone]));
        // 例外抛出异常，有限的校验
        throw new InvalidPinyinException("无效拼音：" + split);
    }
}