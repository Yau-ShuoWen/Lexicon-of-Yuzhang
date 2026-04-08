package com.shuowen.yuzong.Tool.dataStructure.text;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shuowen.yuzong.Tool.JavaUtilExtend.NullTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import com.shuowen.yuzong.Tool.OrthoCharset;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.error.IllegalStringException;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Twin;
import lombok.Getter;
import lombok.ToString;

import static com.shuowen.yuzong.Tool.ProofreadTool.escapeCharTraslate;

/**
 * 简体繁体对<br>
 * 有的时候没有别的事情，单纯校验一下，并且少写一个三元表达式
 */
@Getter
@ToString
public class ScTcText
{
    private UString sc;
    private UString tc;

    public ScTcText()
    {
    }

    /**
     * 标准构造方法，由json对象构造而来
     */
    @JsonCreator
    public ScTcText(@JsonProperty ("sc") String sc,
                    @JsonProperty ("tc") String tc)
    {
        NullTool.checkNotNull(sc, tc);
        this.sc = UString.of(sc);
        this.tc = UString.of(tc);
        if (sc.length() != tc.length()) throw new IllegalStringException(String.format("""
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
        this.sc = escapeCharTraslate(this.tc, Language.TC, new OrthoCharset());
    }

    /**
     * 给出繁体，繁体简体，通过方言判断翻译特点
     */
    public ScTcText(String tc, Dialect d)
    {
        this.tc = UString.of(tc);
        this.sc = escapeCharTraslate(this.tc, Language.TC, new OrthoCharset(d));
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
                escapeCharTraslate(UString.of(tc), Language.TC, new OrthoCharset()) :
                UString.of(tc);
    }

    public static UString get(String tc, Dialect d, Language l)
    {
        return l.isSimplified() ?
                escapeCharTraslate(UString.of(tc), Language.TC, new OrthoCharset(d)) :
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
}
