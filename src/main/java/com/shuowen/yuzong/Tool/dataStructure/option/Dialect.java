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
    NAM("nam", NamStyle.class, NamPinyin.class, NamPinyin::tryOf, NamStyle::createStyle,
            "ncdict", 7, 2);

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

    /**
     * 返回{@code code}里从头开始多少位是声母编码，剩下的就是介韵母的编码长度了<p>
     * 这里名称只是因为声母的英文 {@code initial} 的写法，和初始化无关
     */
    @Getter
    private final int initialLength;


    @SuppressWarnings ("unchecked")
    <U extends PinyinStyle, T extends UniPinyin<U>>
    Dialect(
            String code,
            Class<U> styleClass,
            Class<T> pinyinClass,
            Function<String, Maybe<T>> pinyinTryCreator,
            Function<PinyinParam, U> styleCreator,
            String defaultDict,
            int toneAmount,
            int initialLength
    )
    {
        this.code = code;
        this.styleClass = styleClass;
        this.pinyinClass = pinyinClass;
        this.pinyinTryCreator = (Function) pinyinTryCreator;
        this.styleCreator = styleCreator;
        this.defaultDict = defaultDict;
        this.toneAmount = toneAmount;
        this.initialLength = initialLength;
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

    /**
     * 当流程本身不能保证这个字符串是有效的拼音的时候（所有输入的内容），使用这个
     * @return 需要解析最后是否成功
     */
    @SuppressWarnings ("unchecked")
    public <U extends PinyinStyle, T extends UniPinyin<U>> Maybe<T> tryCreatePinyin(String py)
    {
        return (Maybe<T>) pinyinTryCreator.apply(py);
    }

    /**
     * 当从流程本身就能保证这个字符串是有效的拼音的时候（从数据库里拿出来的数据），使用这个
     * @return 直接返回内容，不需要解析，但是如果是无效的，就直接报异常，说明流程的漏洞把缺陷的拼音存进去了
     */
    @SuppressWarnings ("unchecked")
    public <U extends PinyinStyle, T extends UniPinyin<U>> T trustedCreatePinyin(String py)
    {
        var pinyin = tryCreatePinyin(py);
        if (pinyin.isEmpty()) throw new IllegalArgumentException("来自信任端的拼音无效");
        return (T) pinyin.getValue();
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
