package com.shuowen.yuzong.Linguistics.Format;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shuowen.yuzong.Tool.dataStructure.option.Scheme;
import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonIgnoreProperties (ignoreUnknown = true)
@EqualsAndHashCode (callSuper = true)
@Data
public class NamStyle extends PinyinStyle
{
    //↓ DOTO 之后需要把他给成protect，放进同一个包里
    public enum Style
    {
        DISPALY, KEYBOAD, DEBUG
    }

    private Style style;

    private NamStyle(Style style)
    {
        this.style = style;
    }

    public static NamStyle createStyle(Scheme scheme)
    {
        return switch (scheme)
        {
            case DISPLAY -> new NamStyle(Style.DISPALY);
            case KEYBOARD -> new NamStyle(Style.KEYBOAD);
            case DEBUG -> new NamStyle(Style.DEBUG);
        };
    }
}
