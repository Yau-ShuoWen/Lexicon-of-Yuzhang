package com.shuowen.yuzong.util.text;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.err.IllegalStringException;
import com.shuowen.yuzong.util.ext.other.NullTool;
import com.shuowen.yuzong.util.tuple.Twin;
import lombok.Data;

import java.util.function.Function;

/**
 * 简体繁体对
 */
@Data
public class ScTcText
{
    private final UString sc;
    private final UString tc;
    @JsonInclude (JsonInclude.Include.NON_NULL)
    private final String machineSide;

    /**
     * 标准构造方法，由json对象构造而来
     */
    @JsonCreator
    public ScTcText(@JsonProperty ("sc") String sc,
                    @JsonProperty ("tc") String tc,
                    @JsonProperty ("machineSide") String machineSide)
    {
        NullTool.checkNotNull(false, sc, tc);
        this.sc = UString.of(sc);
        this.tc = UString.of(tc);
        this.machineSide = machineSide;
        if (this.sc.length() != this.tc.length()) throw new IllegalStringException(String.format("""
                        文本框错误：
                        繁体：%s
                        简体：%s
                        简体、繁体体文本长度不等
                        提交的之前请保证每一个简繁框都是✅
                        使用Ctrl+Enter提交
                        """,
                StringTool.limitLength(tc, 15, "……"),
                StringTool.limitLength(sc, 15, "……")
        ));
    }

    public ScTcText(String sc, String tc)
    {
        this(sc, tc, null);
    }

    /**
     * 给出繁体，繁体简体，自动机翻
     */
    public ScTcText(String tc)
    {
        this.tc = UString.of(tc);
        this.sc = Translate.toSc(this.tc);
        this.machineSide = null;
    }

    /**
     * 给出繁体，繁体简体，通过方言判断翻译特点
     */
    public ScTcText(String tc, Dialect d)
    {
        this.tc = UString.of(tc);
        this.sc = Translate.toSc(this.tc, d);
        this.machineSide = null;
    }

    public ScTcText(UString tc, UString sc)
    {
        NullTool.checkNotNull(false, sc, tc);
        this.sc = sc;
        this.tc = tc;
        this.machineSide = null;
    }

    /**
     * 在枚举类初始化、单独开一个main函数测试的时候，数据库用不了，这时候任何形式的OrthoChaset会出错，所以用常规机翻
     */
    public static ScTcText offline(String tc)
    {
        return new ScTcText(Translate.toSc(tc), tc);
    }

    public UString get(Language l)
    {
        return l.isSimplified() ? sc : tc;
    }

    @JsonIgnore
    public Twin<UString> getTwin()
    {
        return Twin.of(sc, tc);
    }

    /**
     * 对一个简单的字符串做机翻
     */
    public static UString get(String tc, Language l)
    {
        var theTc = UString.of(tc);
        return l.isSimplified() ? Translate.toSc(theTc) : theTc;
    }

    /**
     * 对于最简单的硬编码字符，不需要机翻，甚至不需要转UString，这是为了节约其他地方的三元表达式
     */
    public static String get(String tc, String sc, Language l)
    {
        return l.isSimplified() ? sc : tc;
    }

    public static UString get(String tc, Dialect d, Language l)
    {
        return l.isSimplified() ?
                Translate.toSc(UString.of(tc), d) :
                UString.of(tc);
    }

    public int length()
    {
        return sc.length();
    }

    public static String emptyJson()
    {
        return """
                {"sc": "", "tc": ""}
                """;
    }

    public ScTcText map(Function<String, String> fun)
    {
        return new ScTcText(fun.apply(sc.toString()), fun.apply(tc.toString()), machineSide);
    }

    public <T> Twin<T> mapToOther(Function<String, T> fun)
    {
        return Twin.of(fun.apply(sc.toString()), fun.apply(tc.toString()));
    }
}
