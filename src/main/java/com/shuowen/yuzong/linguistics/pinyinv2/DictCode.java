package com.shuowen.yuzong.linguistics.pinyinv2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.text.ScTcText;
import lombok.Getter;

@Getter
public enum DictCode
{
    NCDICT("南昌方言詞典", "tc"),
    NCSTUDY("南昌方言研究", "sc"),
    NCPHON("南昌音系", "tc"),
    NCPHON_Y("南昌音系(严式标音)", "tc"),
    NCRECORD("南昌話音檔", "tc"),
    NCDIALSTD("漢語方言規範", "tc"),
    CNDIALDICT("漢語方音字彙第二版", "tc"),
    GANSUM("贛方言概要", "tc"),

    ;
    private final ScTcText bookname;
    private final Language language;

    DictCode(String name, String language)
    {
        this.bookname = new ScTcText(name);
        this.language = Language.of(language);
    }

    @JsonCreator
    public static DictCode of(String code)
    {
        for (var dict : values())
        {
            if (dict.name().equalsIgnoreCase(code)) return dict;
        }
        throw new IllegalArgumentException("辞书代号无效：" + code);
    }

    @JsonValue
    @Override
    public String toString()
    {
        return name().toLowerCase();
    }
}
