package com.shuowen.yuzong.util.text;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import lombok.Getter;

@Getter
public class ScTcChar
{
    private final UChar sc;
    private final UChar tc;

    @JsonCreator
    public ScTcChar(@JsonProperty ("sc") String sc,
                    @JsonProperty ("tc") String tc)
    {
        this.sc = UChar.of(sc);
        this.tc = UChar.of(tc);
    }

    public UChar get(Language l)
    {
        return l.isSimplified() ? sc : tc;
    }

    public static String emptyJson()
    {
        return """
                {"sc": "", "tc": ""}
                """;
    }

    public ScTcText toText()
    {
        return new ScTcText(sc.toUString(), tc.toUString());
    }
}
