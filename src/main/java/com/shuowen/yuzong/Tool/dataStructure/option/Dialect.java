package com.shuowen.yuzong.Tool.dataStructure.option;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.Linguistics.Format.PinyinParam;
import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Scheme.NamPinyin;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
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
    NAM("nam", NamStyle.class, NamPinyin.class, NamPinyin::of, NamStyle::createStyle, "ncdict");

    private final String code;
    @Getter
    private final Class<? extends PinyinStyle> styleClass;
    @Getter
    private final Class<? extends UniPinyin<?>> pinyinClass;
    private final Function<String, ? extends UniPinyin<?>> pinyinCreator;
    private final Function<PinyinParam, ? extends PinyinStyle> styleCreator;
    @Getter
    private final String defaultDict;


    <U extends PinyinStyle, T extends UniPinyin<U>>
    Dialect(String code,
            Class<U> styleClass,
            Class<T> pinyinClass,
            Function<String, T> pinyinCreator,
            Function<PinyinParam, U> styleCreator,
            String defaultDict
    )
    {
        this.code = code;
        this.styleClass = styleClass;
        this.pinyinClass = pinyinClass;
        this.pinyinCreator = pinyinCreator;
        this.styleCreator = styleCreator;
        this.defaultDict = defaultDict;
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
    public <U extends PinyinStyle, T extends UniPinyin<U>> T createPinyin(String py)
    {
        return (T) pinyinCreator.apply(py);
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
