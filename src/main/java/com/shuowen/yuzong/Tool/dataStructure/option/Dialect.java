package com.shuowen.yuzong.Tool.dataStructure.option;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Scheme.NamPinyin;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;
import lombok.Getter;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 方言代码，提供未来的扩展
 * <ul>
 * <li> {@code NAM} 南昌话 </li>
 * <li> {@code NIL} 无效方言</li>
 * </ul>
 */
public enum Dialect
{
    NAM("nam", NamStyle.class, NamPinyin.class, NamPinyin::of, NamStyle::getStandardStyle, "ncdict"),
    NIL("null", PinyinStyle.class, UniPinyin.class, s -> null, () -> null, "");

    private final String code;

    @Getter
    private final Class<? extends PinyinStyle> styleClass;
    @Getter
    private final Class<? extends UniPinyin<?>> pinyinClass;
    private final Function<String, ? extends UniPinyin<?>> factory;
    private final Supplier<? extends PinyinStyle> styleSupplier;
    private final String defaultDict;


    <U extends PinyinStyle, T extends UniPinyin<U>>
    Dialect(String code,
            Class<U> styleClass,
            Class<T> pinyinClass,
            Function<String, T> factory,
            Supplier<U> styleSupplier,
            String defaultDict
    )
    {
        this.code = code;
        this.styleClass = styleClass;
        this.pinyinClass = pinyinClass;
        this.factory = factory;
        this.styleSupplier = styleSupplier;
        this.defaultDict = defaultDict;
    }

    public static Dialect of(String s)
    {
        if (s == null) return NIL;

        s = s.trim().toLowerCase();
        for (var l : values())
            if (l.code.equals(s)) return l;

        return NIL;
    }

    public boolean isValid()
    {
        return !"null".equals(code);
    }

    @Override
    public String toString()
    {
        return code;
    }

    private void check()
    {
        if (!isValid()) throw new RuntimeException("无效方言代码");
    }

    @SuppressWarnings ("unchecked")
    public <U extends PinyinStyle, T extends UniPinyin<U>> Function<String, T> getFactory()
    {
        check();
        return (Function<String, T>) factory;
    }

    @SuppressWarnings ("unchecked")
    public <U extends PinyinStyle> U getStyle()
    {
        check();
        return (U) styleSupplier.get();
    }

    public String getDefaultDict()
    {
        check();
        return defaultDict;
    }
}
