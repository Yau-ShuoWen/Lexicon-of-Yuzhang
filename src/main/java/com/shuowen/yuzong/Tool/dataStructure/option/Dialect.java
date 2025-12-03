package com.shuowen.yuzong.Tool.dataStructure.option;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Scheme.NamPinyin;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;

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
    NAM("nam", NamStyle.class, NamPinyin.class, NamPinyin::of, NamStyle::getStandardStyle),
    NIL("null", PinyinStyle.class, UniPinyin.class, s -> null, () -> null);

    private final String code;

    public final Class<? extends PinyinStyle> styleClass;
    public final Class<? extends UniPinyin<?>> pinyinClass;
    public final Function<String, ? extends UniPinyin<?>> factory;
    public final Supplier<? extends PinyinStyle> styleSupplier;


    <U extends PinyinStyle, T extends UniPinyin<U>>
    Dialect(String code,
            Class<U> styleClass,
            Class<T> pinyinClass,
            Function<String, T> factory,
            Supplier<U> styleSupplier
    )
    {
        this.code = code;
        this.styleClass = styleClass;
        this.pinyinClass = pinyinClass;
        this.factory = factory;
        this.styleSupplier = styleSupplier;
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

    @SuppressWarnings ("unchecked")
    public <U extends PinyinStyle, T extends UniPinyin<U>> Function<String, T> getFactory()
    {
        return (Function<String, T>) factory;
    }
}
