package com.shuowen.yuzong.Tool.dataStructure.text;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import com.shuowen.yuzong.Tool.OrthoCharset;
import com.shuowen.yuzong.Tool.dataStructure.ErrorInfo;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.error.IllegalStringException;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import lombok.Getter;

import static com.shuowen.yuzong.Tool.ProofreadTool.escapeCharTraslate;

/**
 * 简体繁体对<br>
 * 有的时候没有别的事情，单纯校验一下，并且少写一个三元表达式
 */
@Getter
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
        StringTool.checkTrimValid(sc, tc);
        this.sc = UString.of(sc);
        this.tc = UString.of(tc);
        if (sc.length() != tc.length())
            throw new IllegalStringException("简体繁体字符串长度不等");
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
}
