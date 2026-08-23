package com.shuowen.yuzong.ysw.data.domain.diary;

public enum DiaryViewMode
{
    SELF,
    FRIEND,
    STRANGER;

    public static DiaryViewMode of(String value)
    {
        if (value == null || value.trim().isEmpty())
        {
            return STRANGER;
        }

        return switch (value.trim().toLowerCase())
        {
            case "self", "private", "me" -> SELF;
            case "friend", "friends" -> FRIEND;
            default -> STRANGER;
        };
    }

    public static DiaryViewMode clamp(DiaryViewMode requested, DiaryViewMode allowed)
    {
        if (allowed == SELF)
        {
            return requested;
        }
        if (allowed == FRIEND)
        {
            return requested == SELF ? FRIEND : requested;
        }
        return STRANGER;
    }
}
