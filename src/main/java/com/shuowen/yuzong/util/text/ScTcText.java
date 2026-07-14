package com.shuowen.yuzong.util.text;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shuowen.yuzong.util.ext.other.NullTool;
import com.shuowen.yuzong.util.err.IllegalStringException;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Twin;
import lombok.Data;

import java.util.function.Function;

import static com.shuowen.yuzong.util.text.ProofreadTool.escapeCharTraslate;
import static com.shuowen.yuzong.util.text.ProofreadTool.useHanlpTranslate;

/**
 * 简体繁体对<br>
 * 有的时候没有别的事情，单纯校验一下，并且少写一个三元表达式
 */
@Data
public class ScTcText
{
    private final UString sc;
    private final UString tc;

    /**
     * 标准构造方法，由json对象构造而来
     */
    @JsonCreator
    public ScTcText(@JsonProperty ("sc") String sc,
                    @JsonProperty ("tc") String tc)
    {
        NullTool.checkNotNull(false, sc, tc);
        this.sc = UString.of(sc);
        this.tc = UString.of(tc);
        if (this.sc.length() != this.tc.length()) throw new IllegalStringException(String.format("""
                        文本框錯誤：
                        繁體：%s
                        簡體：%s
                        简体、繁体體文本長度不等
                        提交的之前請保證每一個簡繁框都是✅
                        使用Ctrl+Enter提交
                        """,
                StringTool.limitLength(tc, 15, "……"),
                StringTool.limitLength(sc, 15, "……")
        ));
    }

    /**
     * 给出繁体，繁体简体，自动机翻
     */
    public ScTcText(String tc)
    {
        this.tc = UString.of(tc);
        this.sc = escapeCharTraslate(this.tc, Language.TC, OrthoCharset.of());
    }

    /**
     * 给出繁体，繁体简体，通过方言判断翻译特点
     */
    public ScTcText(String tc, Dialect d)
    {
        this.tc = UString.of(tc);
        this.sc = escapeCharTraslate(this.tc, Language.TC, OrthoCharset.of(d));
    }

    public ScTcText(UString tc, UString sc)
    {
        NullTool.checkNotNull(false, sc, tc);
        this.sc = sc;
        this.tc = tc;
    }

    /**
     * 在枚举初始化的时候，KV用不了，这时候自定义OrthoChaset会出错，所以用常规机翻
     */
    public static ScTcText forEnum(String tc)
    {
        return new ScTcText(useHanlpTranslate(tc, Language.TC), tc);
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
        return l.isSimplified() ?
                escapeCharTraslate(UString.of(tc), Language.TC, OrthoCharset.of()) :
                UString.of(tc);
    }

    /**
     * 对于最简单的硬编码字符，不需要机翻，甚至不需要转UString
     */
    public static String get(String tc, String sc, Language l)
    {
        return l.isSimplified() ? sc : tc;
    }

    public static UString get(String tc, Dialect d, Language l)
    {
        return l.isSimplified() ?
                escapeCharTraslate(UString.of(tc), Language.TC, OrthoCharset.of(d)) :
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
        return new ScTcText(fun.apply(sc.toString()), fun.apply(tc.toString()));
    }

    public <T> Twin<T> mapToOther(Function<String, T> fun)
    {
        return Twin.of(fun.apply(sc.toString()), fun.apply(tc.toString()));
    }
}
