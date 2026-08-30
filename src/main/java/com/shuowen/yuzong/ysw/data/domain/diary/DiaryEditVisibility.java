package com.shuowen.yuzong.ysw.data.domain.diary;

public enum DiaryEditVisibility
{
    PRIVATE("private"),
    FRIEND("friend"),
    STRANGER("stranger");

    private final String value;

    DiaryEditVisibility(String value)
    {
        this.value = value;
    }

    public String value()
    {
        return value;
    }

    public static DiaryEditVisibility of(String value)
    {
        if (value == null)
        {
            throw new IllegalArgumentException("公开范围不能为空");
        }

        return switch (value.trim().toLowerCase())
        {
            case "private", "self" -> PRIVATE;
            case "friend", "friends" -> FRIEND;
            case "stranger", "public" -> STRANGER;
            default -> throw new IllegalArgumentException("公开范围无效");
        };
    }
}
