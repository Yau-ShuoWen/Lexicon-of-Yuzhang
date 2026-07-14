package com.shuowen.yuzong.Tool.dataStructure.option;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.shuowen.yuzong.util.text.ScTcText;
import lombok.Getter;

public enum NoteTag
{
    USAGE("usage", "用法說明"),
    PRONUN("pronun", "讀音說明"),
    STRUCT("struct", "詞語結構"),
    SOURCE("source", "字源考證"),
    HISTORY("history", "歷史演變"),
    PROVERB("proverb", "相關俗語"),
    ;

    private final String code;
    @Getter
    private final ScTcText name;

    NoteTag(String code, String name)
    {
        this.code = code;
        this.name = ScTcText.forEnum(name);
    }

    @JsonCreator
    public static NoteTag of(String s)
    {
        for (var i : NoteTag.values())
        {
            if (i.code.contentEquals(s)) return i;
        }
        throw new RuntimeException("");
    }

    @JsonValue
    public String toString()
    {
        return code;
    }
}
