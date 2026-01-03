package com.shuowen.yuzong.Tool.dataStructure.option;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.Linguistics.Format.PinyinParam;
import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Scheme.NamPinyin;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import lombok.Getter;

import java.util.*;
import java.util.function.Function;

/**
 * 方言代码，提供未来的扩展
 * <ul>
 * <li> {@code NAM} 南昌话 </li>
 * </ul>
 */
public enum Dialect
{
    NAM("nam", NamStyle.class, NamPinyin.class, NamPinyin::tryOf, NamStyle::createStyle, "ncdict", 7);

    private final String code;
    @Getter
    private final Class<? extends PinyinStyle> styleClass;
    @Getter
    private final Class<? extends UniPinyin<?>> pinyinClass;
    private final Function<String, Maybe<?>> pinyinTryCreator;
    private final Function<PinyinParam, ? extends PinyinStyle> styleCreator;
    @Getter
    private final String defaultDict;

    /**
     * 返回声调的数量<br>
     * 不包含轻声，这样好循环: {@code [0, tonesAmount()]}
     */
    @Getter
    private final int toneAmount;

    @SuppressWarnings ("unchecked")
    <U extends PinyinStyle, T extends UniPinyin<U>>
    Dialect(
            String code,
            Class<U> styleClass,
            Class<T> pinyinClass,
            Function<String, Maybe<T>> pinyinTryCreator,
            Function<PinyinParam, U> styleCreator,
            String defaultDict,
            int toneAmount
    )
    {
        this.code = code;
        this.styleClass = styleClass;
        this.pinyinClass = pinyinClass;
        this.pinyinTryCreator = (Function) pinyinTryCreator;
        this.styleCreator = styleCreator;
        this.defaultDict = defaultDict;
        this.toneAmount = toneAmount;
    }


    public static Dialect of(String s)
    {
        StringTool.checkTrimValid(s);
        return switch (s.toLowerCase().trim())
        {
            case "nam" -> NAM;
            default -> throw new IllegalArgumentException("方言代号不正确");
        };
    }

    @Override
    public String toString()
    {
        return code;
    }

    @SuppressWarnings ("unchecked")
    public <U extends PinyinStyle, T extends UniPinyin<U>> Maybe<T> tryCreatePinyin(String py)
    {
        return (Maybe<T>) pinyinTryCreator.apply(py);
    }

    @SuppressWarnings ("unchecked")
    public <U extends PinyinStyle> U createStyle(PinyinParam param)
    {
        return (U) styleCreator.apply(param);
    }

    @SuppressWarnings ("unchecked")
    public <U extends PinyinStyle> U castStyle(PinyinStyle style)
    {
        if (!styleClass.isInstance(style))
            throw new IllegalArgumentException("方言：" + this + " 和格式：" + style.getClass().getSimpleName() + "不匹配");
        else return (U) styleClass.cast(style);
    }

    public static List<Dialect> getList()
    {
        return List.of(NAM);
    }
}
