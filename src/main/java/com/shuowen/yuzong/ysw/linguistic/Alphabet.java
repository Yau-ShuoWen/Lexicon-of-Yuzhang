package com.shuowen.yuzong.ysw.linguistic;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import lombok.Data;
import lombok.Getter;

import java.util.*;

@Getter
@JsonFormat (shape = JsonFormat.Shape.OBJECT)
public enum Alphabet
{
    // 汉语标注方案：当代
    PinYin("漢語拼音", "pinyin", Type.HanYu, true, List.of("文字標註讀音|format")),
    BoPoMoFo("注音符號", "bopomofo", Type.HanYu, false, List.of("文字標註讀音|format")),

    // 汉语标注方案：历史
    Romatzyh("國語羅馬字", "romatzyh", Type.HanYu, true, List.of("漢語文本→轉寫|format")),
    Wade("威妥瑪拼音", "wade", Type.HanYu, true, List.of("漢語文本→轉寫|format")),
    Postal("郵政式拼音", "postal", Type.HanYu, true, List.of()),
    LatinXua("北方話拉丁化新文字", "latinxua", Type.HanYu, true, List.of()),

    // 汉语标注方案：拉中拉
    ZhuyinII("注音第二式", "bopomofo2", Type.HanYu, true, List.of("文字標註讀音|format")),
    TYPinyin("通用拼音", "typinyin", Type.HanYu, true, List.of("文字標註讀音|format")),

    // 汉语标注方案：外國
    HanYale("漢語耶魯拼音", "han-yale", Type.HanYu, true, List.of()),
    HanEFEO("法國遠東學院拼音", "han-efeo", Type.HanYu, true, List.of()),

    HanRussia("漢語西里爾轉寫（俄羅斯方案）", "han-russia", Type.HanYu, true, List.of("漢語文本→轉寫|format")),
//    HanRussiaNew("漢語西里爾轉寫（俄羅斯新版）", "han-russia-new", Type.HanYu, true, List.of("漢語文本→轉寫|format")),
//    HanUkraine("漢語西里爾轉寫（烏克蘭方案）", "han-ukraine", Type.HanYu, true, List.of("漢語文本→轉寫|format")),
//    HanBelarus("漢語西里爾轉寫（白俄羅斯方案）", "han-belarus", Type.HanYu, true, List.of("漢語文本→轉寫|format")),
//    HanSerbia("漢語西里爾轉寫（塞爾維亞方案）", "han-serbia", Type.HanYu, true, List.of("漢語文本→轉寫|format")),
//    HanMavrfonis("漢語西里爾轉寫（馬其頓方案）", "han-mavrfonis", Type.HanYu, true, List.of("漢語文本→轉寫|format")),
//    HanBulgaria("漢語西里爾轉寫（保加利亞方案）", "han-bulgaria", Type.HanYu, true, List.of("漢語文本→轉寫|format")),

    // 古代汉语标注方案
    MiddleChinese("中古漢語", "middle-chinese", Type.History, false, List.of()),


    // 方言拼音方案
    LanCong("南昌話拼音", "lac", Type.Dialect, true, List.of()),
    CenDu("成都話拼音", "ced", Type.Dialect, true, List.of()),
    GwongZau("粵語拼音（廣州方案）", "wade", Type.Dialect, true, List.of()),
    HoengGong("粵語拼音（香港教育部方案）", "wade", Type.Dialect, true, List.of()),
    //    Yale(),
    //    YaleAffix(),
    //2025/06/02手机照片
    // 四川話拉丁化新文字

    // 日语
    HiRaGaNa("日語平假名", "hiragana", Type.CJKV, false, List.of()),
    KaTaKaNa("日語片假名", "katakana", Type.CJKV, false, List.of()),
    GaiRaiGo("片假名外來語擴展", "gairaigo", Type.CJKV, false, List.of()),

    // 韩语
    HanGul("韓語諺文", "hangul", Type.CJKV, false, List.of()),
    JungSe("中世朝鮮語", "jungse", Type.CJKV, false, List.of()),

    // 越南语
    VietNam("越南國語字", "vietnam", Type.CJKV, true, List.of()),

    // 泰语+缅甸语+高棉语
    //  POGA("藏語")

    // 欧洲语言
    ENGLISH("英語字母", "english", Type.Europe, true, List.of()),
    ENGLISHITA("英语ITA字母", "english-ita", Type.Europe, true, List.of()),
    ITALIANO("意大利語字母", "italiano", Type.Europe, true, List.of()),
    ESPANOL("西班牙語字母", "espanol", Type.Europe, true, List.of()),

    SuZhouCode("蘇州碼子", "suzhou-code", Type.Number, false, List.of("阿拉伯數字→蘇州碼子|encode", "蘇州碼子→阿拉伯數字|decode")),
    RomanNumber("羅馬數字", "roman-number", Type.Number, true, List.of("阿拉伯數字→羅馬數字|encode"/*, "羅馬數字→阿拉伯數字|decode"*/)),
    NumberSystem("其他進制", "number-system", Type.Number, true, List.of("十進制→其他進制|decimal", "二進制→其他進制|binary", "八進制→其他進制|octal", "十六進制→其他進制|hexadecimal")),

    ;

    enum Type
    {
        HanYu,
        History,
        Dialect,
        CJKV,

        Europe,
        Number,
    }

    private final ScTcText name;
    private final String code;
    private final Type type;
    private final Boolean latin;
    private final List<Pair<ScTcText, String>> trans;

    Alphabet(String name, String code, Type type, Boolean latin, List<String> trans)
    {
        this.name = ScTcText.forEnum(name);
        this.code = code;
        this.type = type;
        this.latin = latin;
        this.trans = ListTool.mapping(trans, str ->
        {
            String[] s = str.split("\\|");
            return Pair.of(ScTcText.forEnum(s[0]), s[1]);
        });
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

    @Data
    public static class AlphabetT
    {
        private UString name;
        private String code;
        private List<Pair<UString, String>> trans;
    }

    public AlphabetT toTrans(Language l)
    {
        AlphabetT ans = new AlphabetT();
        ans.name = name.get(l);
        ans.code = code;
        ans.trans = ListTool.mapping(trans, i -> Pair.of(i.getLeft().get(l), i.getRight()));
        return ans;
    }
}
