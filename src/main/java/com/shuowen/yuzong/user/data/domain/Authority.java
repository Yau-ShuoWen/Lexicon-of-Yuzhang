package com.shuowen.yuzong.user.data.domain;

import java.util.Locale;

public enum Authority
{
    BLOG_READ_PUBLIC("blog.read.public"),
    BLOG_READ_FRIENDS("blog.read.friends"),
    BLOG_READ_PRIVATE("blog.read.private"),
    DEV_ACCESS("dev.access"),
    DICT_EDIT_VIEW("dict.edit.view"),
    DICT_EDIT_WRITE("dict.edit.write"),
    DICT_ACCESS_LAC("dialect.lac.access"),
    DICT_ACCESS_CED("dialect.ced.access"),
    ADMIN_ACCESS("admin.access"),
    ADMIN("admin"),
    NO("no");

    private final String code;

    Authority(String code)
    {
        this.code = code.trim().toLowerCase(Locale.ROOT);
    }

    public String code()
    {
        return code;
    }

    public boolean matches(String value)
    {
        return value != null && code.equals(value.trim().toLowerCase(Locale.ROOT));
    }

    public static Authority of(String code)
    {
        if (code == null)
        {
            return NO;
        }
        for (var i : values())
        {
            if (i.matches(code))
            {
                return i;
            }
        }
        return NO;
    }
}
