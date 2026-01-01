package com.shuowen.yuzong.data.domain.Pinyin;

import com.shuowen.yuzong.Linguistics.Format.PinyinParam;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Scheme;

import java.util.*;
import java.util.function.BiFunction;

public class PinyinTablizer
{
    public static List<List<PinyinDetail>> getTable(Dialect d)
    {
        return switch (d)
        {
            case NAM -> tableNam();
        };
    }

    public static List<PinyinDetail> getTonePrewiew(Dialect d, String p)
    {
        List<PinyinDetail> result = new ArrayList<>();

        BiFunction<String, Boolean, String> escape =
                (pinyin, handleEscape) -> switch (d)
                {
                    case NAM -> escapeNam(pinyin, handleEscape);
                };


        for (int i = 0; i <= d.createPinyin(p).getTonesNumber(); i++)
        {
            var py = d.createPinyin(escape.apply(p, true) + i);
            if (py.isValid()) result.add(PinyinDetail.exist(
                    escape.apply(PinyinTool.formatPinyin(py, d, PinyinParam.of(Scheme.STANDARD)), false),
                    escape.apply(PinyinTool.formatPinyin(py, d, PinyinParam.of(Scheme.KEYBOARD)), false)
            ));
            else result.add(PinyinDetail.notExist());
        }
        return result;
    }


    private static List<List<PinyinDetail>> tableNam()
    {
        var tone = PinyinDetail.listOf(
                "tone",
                "a|à|á|ǎ|ā|ả|a̋|ȁ",
                "a0|a1|a2|a3|a4|a5|a6|a7"
        );
        var initial = PinyinDetail.listOf(
                "initial",
                "b|p|m|f|d|t|l|g|k|ng|h|j|q|n|x|z|c|s",
                "b|p|m|f|d|t|l|g|k|ng|h|j|q|n|x|z|c|s"
        );
        var lastWithSingle = PinyinDetail.listOf(
                "lastWithSingle",
                "a|o|e|ọ|ẹ|i|u|ü|i",
                "a|o|e|oe|ee|i|u|yu|ii"
        );
        var lastWithDouble = PinyinDetail.listOf(
                "lastWithDouble",
                "ai|uai|ẹi|ui|au|eu|ieu|ẹu|iu",
                "ai|uai|eei|ui|au|eu|ieu|eeu|iu"
        );
        var lastWithNasal = PinyinDetail.listOf(
                "lastWithNasal",
                "an|on|en|ẹn|in|un|ün|ang|ong|ung|iung",
                "an|on|en|een|in|un|yun|ang|ong|ung|iung"
        );
        var lastWithShort = PinyinDetail.listOf(
                "lastWithShort",
                "at|ot|et|ẹt|it|ut|üt|ak|ok|uk|iuk",
                "at|ot|et|eet|it|ut|yut|ak|ok|uk|iuk"
        );
        return List.of(tone, initial, lastWithSingle, lastWithDouble, lastWithNasal, lastWithShort);
    }

    private static String escapeNam(String str, boolean add)
    {
        if (str.contains("ii") || str.contains("zi"))
            return add ? "z" + str : str.replace("z", "");
        else return str;
    }
}
