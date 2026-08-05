package com.shuowen.yuzong.Tool.dataStructure.option;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 两种合适的参数（不考虑地区陆港台）
 * <ul>
 * <li> SC 简体中文 </li>
 * <li> TC 繁体中文 </li>
 * </ul>
 * 不要使用 == 来判断 Language，需要两个分开处理的时候使用{@code isSimplified()}和默认分支
 */
public enum Language
{
    SC("sc"), TC("tc");

    private final String code;

    Language(String code)
    {
        this.code = code;
    }

    @JsonCreator
    public static Language of(String s)
    {
        for (Language l : values())
        {
            if (l.code.equalsIgnoreCase(s)) return l;
        }
        throw new IllegalArgumentException("简繁体代码无效：" + s);
    }

    /**
     * 简体返回繁体，繁体返回简体
     */
    public Language reverse()
    {
        return this == SC ? TC : SC;
    }

    @JsonValue
    @Override
    public String toString()
    {
        return code;
    }

    /**
     * 一般是判断简体字
     */
    public boolean isSimplified()
    {
        return this == SC;
    }
}

