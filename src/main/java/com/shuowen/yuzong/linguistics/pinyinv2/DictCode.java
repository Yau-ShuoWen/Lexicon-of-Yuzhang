package com.shuowen.yuzong.linguistics.pinyinv2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.ext.list.ListTool;
import com.shuowen.yuzong.util.text.ScTcText;
import lombok.Getter;

import java.util.List;

@Getter
public enum DictCode
{
    NCDICT("南昌方言詞典","lac","tc"),
    NCSTUDY("南昌方言研究","lac","sc"),
    NCPHON("南昌音系","lac","tc"),
    NCPHON_Y("南昌音系(严式标音)","lac","tc"),
    NCRECORD("南昌話音檔","lac","tc"),
    NCDIALSTD("漢語方言規範","lac","tc"),
    CNDIALDICT("漢語方音字彙第二版","lac","tc"),
    GANSUM("贛方言概要","lac","tc"),

    ;


    ScTcText bookname;
    List<Dialect> dialects;
    Language language;

    DictCode(String name, String dialects, String language)
    {
        this.bookname = new ScTcText(name);
        this.dialects = ListTool.mapping(dialects.split(" "), Dialect::of);
        this.language = Language.of(language);
    }

    @JsonCreator
    public static DictCode of(String code)
    {
        for (var dict : values())
        {
            if (dict.name().equalsIgnoreCase(code)) return dict;
        }
        throw new IllegalArgumentException("字典代号无效：" + code);
    }

    @JsonValue
    @Override
    public String toString()
    {
        return name().toLowerCase();
    }
}
