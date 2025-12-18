package com.shuowen.yuzong.Tool.dataStructure.option;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Scheme.NamPinyin;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import lombok.Getter;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 方言代码，提供未来的扩展
 * <ul>
 * <li> {@code NAM} 南昌话 </li>
 * </ul>
 */
public enum Dialect
{
    NAM("nam", NamStyle.class, NamPinyin.class,
            NamPinyin::of, NamStyle::getStandardStyle, NamStyle::getKeyboardStyle,
            "ncdict");

    private final String code;
    @Getter
    private final Class<? extends PinyinStyle> styleClass;
    @Getter
    private final Class<? extends UniPinyin<?>> pinyinClass;
    private final Function<String, ? extends UniPinyin<?>> factory;
    private final Supplier<? extends PinyinStyle> standardStyleGetter;
    private final Supplier<? extends PinyinStyle> keyboardStyleGetter;
    @Getter
    private final String defaultDict;


    <U extends PinyinStyle, T extends UniPinyin<U>>
    Dialect(String code,
            Class<U> styleClass,
            Class<T> pinyinClass,
            Function<String, T> factory,
            Supplier<U> standardStyleGetter,
            Supplier<U> keyboardStyleGetter,
            String defaultDict
    )
    {
        this.code = code;
        this.styleClass = styleClass;
        this.pinyinClass = pinyinClass;
        this.factory = factory;
        this.standardStyleGetter = standardStyleGetter;
        this.keyboardStyleGetter = keyboardStyleGetter;
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
    public <U extends PinyinStyle, T extends UniPinyin<U>> Function<String, T> getFactory()
    {
        return (Function<String, T>) factory;
    }

    @SuppressWarnings ("unchecked")
    public <U extends PinyinStyle> U getStyle()
    {
        return (U) standardStyleGetter.get();
    }

    @SuppressWarnings ("unchecked")
    public <U extends PinyinStyle> U getKeyboardStyle()
    {
        return (U) keyboardStyleGetter.get();
    }

    public static List<Dialect> getList()
    {
        return List.of(NAM);
    }
}
