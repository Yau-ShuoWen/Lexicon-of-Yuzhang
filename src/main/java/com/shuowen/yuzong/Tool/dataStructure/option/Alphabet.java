package com.shuowen.yuzong.Tool.dataStructure.option;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import lombok.Getter;

import java.util.*;

@Getter
@JsonFormat (shape = JsonFormat.Shape.OBJECT)
public enum Alphabet
{
    PinYin("漢語拼音", "pinyin", true, true),
    ZhuYin("注音符號", "zhuyin", false, true),
    Romatzyh("國語羅馬字", "romatzyh", true, true),
    Wade("威妥瑪拼音", "wade", true, true),
    Postal("郵政式拼音", "postal", true, false),
    ZhuyinII("注音符號第二式", "zhuyinii", true, true),
    TYPinyin("通用拼音", "typinyin", true, true),

//    LanCong("南昌話拼音", "lac", true, true),
//    CenDu("成都話拼音", "ced", true, true),
//    GwongZau("威妥瑪拼音", "wade", true),
//    HoengGong("威妥瑪拼音", "wade", true),

    MiddleChinese("中古漢語", "middle-chinese", false, false),


    HiRaGaNa("日語平假名", "hiragana", false, false),

    HanGul("韓語諺文", "hangul", false, false),

    ENGLISH("英語字母", "english", true, false),
    ENGLISHITA("英语ITA字母", "english-ita", true, false),

    //  ITALIANO("意大利語字母", "italiano", true),
    ;

    private final ScTcText name;
    private final String code;
    private final Boolean latin;
    private final Boolean transfer;

    Alphabet(String name, String code, Boolean latin, Boolean transfer)
    {
        this.name = new ScTcText(name);
        this.code = code;
        this.latin = latin;
        this.transfer = transfer;
    }

    @JsonCreator
    public static Alphabet of(String s)
    {
        StringTool.checkTrimValid(s);

        for (var i : Alphabet.values())
        {
            if (i.code.equalsIgnoreCase(s))
            {
                return i;
            }
        }
        throw new IllegalArgumentException("方言代号无效：" + s);
    }

    @Override
    public String toString()
    {
        return code;
    }

    public static List<Alphabet> getList()
    {
        return List.of(Alphabet.values());
    }
}
