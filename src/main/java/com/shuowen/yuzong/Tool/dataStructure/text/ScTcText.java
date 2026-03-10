package com.shuowen.yuzong.Tool.dataStructure.text;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shuowen.yuzong.Tool.OrthoCharset;
import com.shuowen.yuzong.Tool.dataStructure.ErrorInfo;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import lombok.Getter;

import static com.shuowen.yuzong.Tool.ProofreadTool.escapeCharTraslate;

/**
 * 简体繁体对
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
        UString.checkLenEqual(ErrorInfo.of("ScTcText构造"), sc, tc);
        this.sc = UString.of(sc);
        this.tc = UString.of(tc);
    }

    /**
     * 给出繁体，繁体简体，自定义翻译方式
     *
     * @param d 如果为null，不启动任何词语
     */
    public ScTcText(String tc, Dialect d)
    {
        this.tc = UString.of(tc);
        this.sc = escapeCharTraslate(this.tc, Language.TC,
                d == null ? new OrthoCharset() : new OrthoCharset(d));
    }

    public UString get(Language l)
    {
        return l.isSimplified() ? sc : tc;
    }
}
