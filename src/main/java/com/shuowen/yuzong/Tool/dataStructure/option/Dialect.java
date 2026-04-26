package com.shuowen.yuzong.Tool.dataStructure.option;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Scheme.SPinyin;
import com.shuowen.yuzong.Linguistics.Scheme.NamPinyin;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.error.InvalidPinyinException;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.data.domain.Reference.DictCode;
import lombok.Getter;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 方言代码，提供未来的扩展
 * <ul>
 * <li> {@code NAM} 南昌话 </li>
 * <li> {@code CED} 成都话 </li>
 * </ul>
 */
@SuppressWarnings ({"unchecked", "rawtypes", "unused"})
public enum Dialect
{
    NAM("南昌話", "nam", NamStyle.class, NamPinyin.class,
            NamPinyin::tryOf, NamStyle::createStyle, NamPinyin::normalize,
            new DictCode("ncdict"), 7, 2);

//   南昌话LAC
//    CED("成都话","ced", CEDStyle.class, CEDPinyin.class,
//            CEDPinyin::tryOf,CEDStyle::createStyle,CEDPinyin::normalize,
//            new DictCode("cddict"),4,2);


    @Getter
    private final ScTcText name;
    private final String code;
    private final Class<? extends PinyinStyle> styleClass;
    private final Class<? extends UniPinyin<?>> pinyinClass;
    private final BiFunction<SPinyin, Boolean, Maybe<?>> pinyinTryCreator;
    private final Function<Scheme, ? extends PinyinStyle> styleCreator;
    private final Function<SPinyin, SPinyin> normalizer;
    @Getter
    private final DictCode defaultDict;

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

    /**
     * 构造函数
     */
    <U extends PinyinStyle, T extends UniPinyin<U>>
    Dialect(String name, String code, Class<U> styleClass, Class<T> pinyinClass,
            BiFunction<SPinyin,Boolean, Maybe<T>> pinyinTryCreator, Function<Scheme, U> styleCreator,
            Function<SPinyin, SPinyin> normalizer,
            DictCode defaultDict, int toneAmount, int initialLength
    )
    {
        this.name = new ScTcText(name);
        this.code = code;
        this.styleClass = styleClass;
        this.pinyinClass = pinyinClass;
        this.pinyinTryCreator = (BiFunction) pinyinTryCreator;
        this.styleCreator = styleCreator;
        this.normalizer = normalizer;
        this.defaultDict = defaultDict;
        this.toneAmount = toneAmount;
        this.initialLength = initialLength;
    }

    @JsonCreator
    public static Dialect of(String s)
    {
        StringTool.checkTrimValid(s);
        return switch (s.toLowerCase().trim())
        {
            case "nam" -> NAM;
            default -> throw new IllegalArgumentException("方言代号无效：" + s);
        };
    }

    @JsonValue
    @Override
    public String toString()
    {
        return code;
    }

    /**
     * 当流程本身不能保证这个字符串是有效的拼音的时候（所有输入的内容），使用这个
     *
     * @return 需要解析最后是否成功
     */
    public <U extends PinyinStyle, T extends UniPinyin<U>> Maybe<T> tryCreatePinyin(SPinyin py)
    {
        return (Maybe<T>) pinyinTryCreator.apply(py,false);
    }

    /**
     * 当从流程本身就能保证这个字符串是有效的拼音的时候（从数据库里拿出来的数据），使用这个
     *
     * @return 直接返回内容，不需要解析，但是如果是无效的，就直接报异常，说明流程的漏洞把缺陷的拼音存进去了
     */
    public <U extends PinyinStyle, T extends UniPinyin<U>> T trustedCreatePinyin(SPinyin py)
    {
        var pinyin = pinyinTryCreator.apply(py,true);
        if (pinyin.isEmpty()) throw new IllegalArgumentException("来自信任端的拼音无效。文本：" + py);
        return (T) pinyin.getValue();
    }

    /**
     * 用于前端传送到后端的拼音检查，如果通过检查，返回拼音，如果没有通过，报错
     *
     * @throws InvalidPinyinException 碰见这个错误不用打印栈
     */
    public <U extends PinyinStyle, T extends UniPinyin<U>> T checkAndCreatePinyin(SPinyin py)
    {
        var pinyin = tryCreatePinyin(py);
        if (pinyin.isEmpty()) throw new InvalidPinyinException(String.format("%s拼音%s无效", getName().getSc(), py));
        return (T) pinyin.getValue();
    }

    /**
     * 根据通用的拼音参数创建方言拼音格式
     */
    public <U extends PinyinStyle> U createStyle(Scheme param)
    {
        return (U) styleCreator.apply(param);
    }

    public SPinyin normalizePinyin(SPinyin s)
    {
        return normalizer.apply(s);
    }

    public static List<Dialect> getList()
    {
        return List.of(NAM);
    }
}
