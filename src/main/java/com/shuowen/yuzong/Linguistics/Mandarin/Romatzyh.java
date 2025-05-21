package com.shuowen.yuzong.Linguistics.Mandarin;

import java.util.HashMap;
import java.util.Map;

public class Romatzyh
{
    static private final Map<String, String[][]> ZHUYIN_DATA = Map.ofEntries(
            Map.entry("帀", new String[][]{{"y", "/"}, {"yr", "/"}, {"yy", "/"}, {"yh", "/"}, {"yq", "/"}}),
            Map.entry("ㄧ", new String[][]{{"i", "i"}, {"yi", "yi"}, {"ii", "yii"}, {"ih", "yih"}, {"iq", "iq"}}),
            Map.entry("ㄨ", new String[][]{{"u", "u"}, {"wu", "wu"}, {"uu", "wuu"}, {"uh", "wuh"}, {"uq", "uq"}}),
            Map.entry("ㄩ", new String[][]{{"iu", "iu"}, {"yu", "yu"}, {"eu", "yeu"}, {"iuh", "yuh"}, {"yuq", "yuq"}}),
            Map.entry("ㄚ", new String[][]{{"a", "a"}, {"ar", "ar"}, {"aa", "aa"}, {"ah", "ah"}, {"aq", "aq"}}),
            Map.entry("ㄛ", new String[][]{{"o", "o"}, {"or", "or"}, {"oo", "oo"}, {"oh", "oh"}, {"oq", "oq"}}),
            Map.entry("ㄜ", new String[][]{{"e", "e"}, {"er", "er"}, {"ee", "ee"}, {"eh", "eh"}, {"/", "/"}}),
            Map.entry("ㄝ", new String[][]{{"é", "é"}, {"ér", "ér"}, {"éé", "éé"}, {"éh", "éh"}, {"éq", "éq"}}),
            Map.entry("ㄞ", new String[][]{{"ai", "ai"}, {"air", "air"}, {"ae", "ae"}, {"ay", "ay"}, {"/", "/"}}),
            Map.entry("ㄟ", new String[][]{{"ei", "ei"}, {"eir", "eir"}, {"eei", "eei"}, {"ey", "ey"}, {"/", "/"}}),
            Map.entry("ㄠ", new String[][]{{"au", "au"}, {"aur", "aur"}, {"ao", "ao"}, {"aw", "aw"}, {"/", "/"}}),
            Map.entry("ㄡ", new String[][]{{"ou", "ou"}, {"our", "our"}, {"oou", "oou"}, {"ow", "ow"}, {"/", "/"}}),
            Map.entry("ㄢ", new String[][]{{"an", "an"}, {"arn", "arn"}, {"aan", "aan"}, {"ann", "ann"}, {"/", "/"}}),
            Map.entry("ㄣ", new String[][]{{"en", "en"}, {"ern", "ern"}, {"een", "een"}, {"enn", "enn"}, {"/", "/"}}),
            Map.entry("ㄤ", new String[][]{{"ang", "ang"}, {"arng", "arng"}, {"aang", "aang"}, {"anq", "anq"}, {"/", "/"}}),
            Map.entry("ㄥ", new String[][]{{"eng", "eng"}, {"erng", "erng"}, {"eeng", "eeng"}, {"enq", "enq"}, {"/", "/"}}),
            Map.entry("ㄦ", new String[][]{{"el", "el"}, {"erl", "erl"}, {"eel", "eel"}, {"ell", "ell"}, {"/", "/"}}),
            Map.entry("ㄧㄚ", new String[][]{{"ia", "ia"}, {"ya", "ya"}, {"ea", "yea"}, {"iah", "yah"}, {"iaq", "yaq"}}),
            Map.entry("ㄧㄛ", new String[][]{{"io", "io"}, {"yo", "yo"}, {"eo", "yeo"}, {"ioh", "yoh"}, {"ioq", "yoq"}}),
            Map.entry("ㄧㄝ", new String[][]{{"ie", "ie"}, {"ye", "ye"}, {"iee", "yee"}, {"ieh", "yeh"}, {"ieq", "yeq"}}),
            Map.entry("ㄧㄠ", new String[][]{{"iau", "iau"}, {"yau", "yau"}, {"eau", "yeau"}, {"iaw", "yaw"}, {"/", "/"}}),
            Map.entry("ㄧㄡ", new String[][]{{"iou", "iou"}, {"you", "you"}, {"eou", "yeou"}, {"iow", "yow"}, {"/", "/"}}),
            Map.entry("ㄧㄢ", new String[][]{{"ian", "ian"}, {"yan", "yan"}, {"ean", "yean"}, {"iann", "yann"}, {"/", "/"}}),
            Map.entry("ㄧㄣ", new String[][]{{"in", "in"}, {"yn", "yn"}, {"iin", "yiin"}, {"inn", "yinn"}, {"/", "/"}}),
            Map.entry("ㄧㄤ", new String[][]{{"iang", "iang"}, {"yang", "yang"}, {"eang", "yeang"}, {"ianq", "yanq"}, {"/", "/"}}),
            Map.entry("ㄧㄥ", new String[][]{{"ing", "ing"}, {"yng", "yng"}, {"iing", "yiing"}, {"inq", "yinq"}, {"/", "/"}}),
            Map.entry("ㄨㄚ", new String[][]{{"ua", "ua"}, {"wa", "wa"}, {"oa", "woa"}, {"uah", "wah"}, {"uaq", "waq"}}),
            Map.entry("ㄨㄛ", new String[][]{{"uo", "uo"}, {"wo", "wo"}, {"uoo", "woo"}, {"uoh", "woh"}, {"uoq", "woq"}}),
            Map.entry("ㄨㄞ", new String[][]{{"uai", "uai"}, {"wai", "wai"}, {"oai", "woai"}, {"uay", "way"}, {"/", "/"}}),
            Map.entry("ㄨㄟ", new String[][]{{"uei", "uei"}, {"wei", "wei"}, {"oei", "woei"}, {"uey", "wey"}, {"/", "/"}}),
            Map.entry("ㄨㄢ", new String[][]{{"uan", "uan"}, {"wan", "wan"}, {"oan", "woan"}, {"uann", "wan"}, {"/", "/"}}),
            Map.entry("ㄨㄣ", new String[][]{{"uen", "uen"}, {"wen", "wen"}, {"oen", "woen"}, {"uenn", "wenn"}, {"/", "/"}}),
            Map.entry("ㄨㄤ", new String[][]{{"uang", "uang"}, {"wang", "wang"}, {"oang", "woang"}, {"uanq", "wanq"}, {"/", "/"}}),
            Map.entry("ㄨㄥ", new String[][]{{"ong", "ueng"}, {"orng", "weng"}, {"oong", "woeng"}, {"onq", "wenq"}, {"/", "/"}}),
            Map.entry("ㄩㄝ", new String[][]{{"iue", "iue"}, {"yue", "yue"}, {"eue", "yeue"}, {"iueh", "yueh"}, {"iueq", "yueq"}}),
            Map.entry("ㄩㄢ", new String[][]{{"iuan", "iuan"}, {"yuan", "yuan"}, {"euan", "yeuan"}, {"iuann", "yuann"}, {"/", "/"}}),
            Map.entry("ㄩㄣ", new String[][]{{"iun", "iun"}, {"yun", "yun"}, {"eun", "yeun"}, {"iunn", "yunn"}, {"/", "/"}}),
            Map.entry("ㄩㄥ", new String[][]{{"iong", "iong"}, {"yong", "yong"}, {"eong", "yeong"}, {"ionq", "yonq"}, {"/", "/"}})
    );

    static public String toRomatzyh(Zhuyin s)
    {
        if(!s.valid) return "";

        String Sheng, Yun;
        Sheng = switch (s.getSheng())
        {
            case "ㄅ" -> "b";
            case "ㄆ" -> "p";
            case "ㄇ" -> "m";
            case "ㄈ" -> "f";
            case "ㄉ" -> "d";
            case "ㄊ" -> "t";
            case "ㄋ" -> "n";
            case "ㄌ" -> "l";
            case "ㄍ" -> "g";
            case "ㄎ" -> "k";
            case "ㄏ" -> "h";
            case "ㄐ" -> "j";
            case "ㄑ" -> "ch";
            case "ㄒ" -> "sh";
            case "ㄓ" -> "j";
            case "ㄔ" -> "ch";
            case "ㄕ" -> "sh";
            case "ㄖ" -> "r";
            case "ㄗ" -> "tz";
            case "ㄘ" -> "ts";
            case "ㄙ" -> "s";
            case "万" -> "v";
            case "兀" -> "ng";
            case "广" -> "gn";
            default -> "";
        };

        String yun = s.getJie() + s.getYun();

        if (yun.isEmpty())
        {
            yun = switch (s.getSheng())
            {
                case "ㄓ", "ㄔ", "ㄕ", "ㄖ", "ㄗ", "ㄘ", "ㄙ" -> "帀";
                default -> "";
            };
        }
        int tone=s.getTone()-1;
        if(tone==4) tone=0;

        //浊音一声加h
        if (tone == 0)
        {
            Sheng += switch (s.getSheng())
            {
                case "ㄇ", "ㄖ", "ㄌ", "ㄋ", "万", "兀", "广" -> 'h';
                default -> "";
            };
        }
        if(tone==1)
        {
            tone = switch (s.getSheng())
            {
                case "ㄇ", "ㄖ", "ㄌ", "ㄋ", "万", "兀", "广" -> 0;
                default -> 1;
            };
        }


        Yun = ZHUYIN_DATA.get(yun)[tone][Sheng.isEmpty() ? 1 : 0];
        return Sheng+Yun;
    }
}
