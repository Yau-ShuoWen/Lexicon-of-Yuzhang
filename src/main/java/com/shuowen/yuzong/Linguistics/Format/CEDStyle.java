package com.shuowen.yuzong.Linguistics.Format;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shuowen.yuzong.Tool.dataStructure.option.Scheme;
import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonIgnoreProperties (ignoreUnknown = true)
@EqualsAndHashCode (callSuper = true)
@Data
public class CEDStyle extends PinyinStyle
{
    public enum Style
    {
        DISPALY, KEYBOAD, DEBUG
    }

    private Style style;

    private CEDStyle(Style style)
    {
        this.style = style;
    }

    public static CEDStyle createStyle(Scheme scheme)
    {
        return switch (scheme)
        {
            case DISPLAY -> new CEDStyle(Style.DISPALY);
            case KEYBOARD -> new CEDStyle(Style.KEYBOAD);
            case DEBUG -> new CEDStyle(Style.DEBUG);
        };
    }
}
