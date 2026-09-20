package com.shuowen.yuzong.linguistics.pinyinv2;

import com.shuowen.yuzong.dict.data.domain.IPA.IPAFormatter;
import com.shuowen.yuzong.dict.data.domain.IPA.IPASyllStyle;
import com.shuowen.yuzong.dict.data.domain.IPA.IPAToneStyle;
import com.shuowen.yuzong.util.err.InvalidPinyinException;
import com.shuowen.yuzong.util.tuple.Maybe;

/**
 * 南昌话拼音到国际音标的转换。
 * <p>
 * 各资料的静态音值保存在 {@code linguistics/lac-ipa.csv}，这里仅负责根据已经结构化的拼音选择并组合数据。
 */
public final class LACIPA
{
    private LACIPA()
    {
    }

    public static String handle(LACPinyin pinyin)
    {
        return handle(DictCode.NCDICT, pinyin);
    }

    public static String handle(DictCode dict, LACPinyin pinyin)
    {
        Maybe<String> result = query(
                pinyin, dict, IPASyllStyle.CHINESE_SPECIAL, IPAToneStyle.FIVE_DEGREE_LINE
        );
        if (result.isEmpty())
            throw new InvalidPinyinException(dict + "没有记录南昌话拼音：" + pinyin);
        return result.getValue();
    }

    public static Maybe<String> query(LACPinyin pinyin)
    {
        return query(
                pinyin, DictCode.NCDICT,
                IPASyllStyle.CHINESE_SPECIAL, IPAToneStyle.FIVE_DEGREE_LINE
        );
    }

    public static Maybe<String> query(LACPinyin pinyin, DictCode dict)
    {
        return query(
                pinyin, dict,
                IPASyllStyle.CHINESE_SPECIAL, IPAToneStyle.FIVE_DEGREE_LINE
        );
    }

    public static Maybe<String> query(
            LACPinyin pinyin,
            DictCode dict,
            IPASyllStyle syllStyle,
            IPAToneStyle toneStyle
    )
    {
        if (dict == DictCode.NCDIALSTD) return Maybe.nothing();

        Maybe<String> rawSyllable = pinyin.yinjie.fold(
                dandu -> find("dandu", dandu.toString(), "", dict),
                yinjie -> join(
                        find("initial", yinjie.getShengmu().name, "", dict),
                        find("final", yinjie.getYun(), "", dict)
                )
        );
        if (rawSyllable.isEmpty()) return Maybe.nothing();

        String syllable = IPAFormatter.formatSyllable(rawSyllable.getValue(), syllStyle);
        return pinyin.yindiao.fold(
                tone -> applyTone(dict, tone, toneStyle, syllable),
                ignored -> Maybe.nothing()
        );
    }

    private static Maybe<String> applyTone(
            DictCode dict,
            LACPinyin.YinDiao tone,
            IPAToneStyle toneStyle,
            String syllable
    )
    {
        if (tone == LACPinyin.YinDiao.$) return Maybe.exist(syllable);

        Maybe<String> template = find("tone", tone.code.toString(), styleKey(toneStyle), dict);
        return template.handleIfExist(value -> value.replace("{syllable}", syllable));
    }

    private static String styleKey(IPAToneStyle style)
    {
        return switch (style)
        {
            case FIVE_DEGREE_LINE -> "five_degree_line";
            case FIVE_DEGREE_NUM -> "five_degree_num";
            case FOUR_CORNER -> "four_corner";
        };
    }

    private static Maybe<String> join(Maybe<String> left, Maybe<String> right)
    {
        if (left.isEmpty() || right.isEmpty()) return Maybe.nothing();
        return Maybe.exist(left.getValue() + right.getValue());
    }

    private static Maybe<String> find(String type, String standard, String style, DictCode dict)
    {
        return LACIPATable.find(type, standard, style, dict);
    }
}
