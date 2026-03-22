package com.shuowen.yuzong.data.domain.Pinyin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Linguistics.Scheme.RPinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.MapTool;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.data.domain.IPA.IPAData;
import com.shuowen.yuzong.data.domain.IPA.IPATool;
import com.shuowen.yuzong.data.domain.Reference.DictCode;
import com.shuowen.yuzong.data.model.IPA.IPAItem;
import com.shuowen.yuzong.service.impl.IPA.IPAService;
import lombok.Data;
import lombok.Getter;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.shuowen.yuzong.Tool.format.JsonTool.readJson;

@Data
public class PinyinDetail
{
    private String key;

    private enum Type
    {INITIAL, LAST, TONE, SPECIAL}

    private Type type;

    @Getter
    private class Info
    {
        String key;
        String standard;
        LinkedHashMap<String, RPinyin> notation;
        TreeMap<String, String> ipa = new TreeMap<>();
        List<UString> note;

        public Info(IPAItem item, final IPAData data)
        {
            Language l = data.getLanguage();

            key = item.getUrl();

            {
                var tmp = readJson(item.getNotation(), new TypeReference<Map<String, String>>() {}, new ObjectMapper());


                Matcher matcher = Pattern.compile("\\[.*?]").matcher(tmp.get("standard"));
                standard = matcher.find() ? matcher.group() : String.format("[%s]", tmp.get("standard"));

                // 当是单独的内容的时候，打拼音标记并加粗，否则直接加粗
                Function<String, String> fun = (String s) ->
                {
                    if (s.contains("[") && s.contains("]"))
                        return s.replaceAll("\\[(.*?)]", "{b [$1]}");
                    else return String.format("{b [%s]}", s);
                };
                notation = MapTool.orderMapOf(
                        ScTcText.get("手寫", l), fun.apply(tmp.get("standard")),
                        ScTcText.get("打字", l), fun.apply(tmp.get("keyboard"))
                );
            }

            for (var i : readJson(item.getInfo(), new TypeReference<Map<DictCode, String>>() {}, new ObjectMapper()).entrySet())
            {
                if ("-".equals(i.getValue())) continue;
                if (type != Type.TONE)
                    ipa.put(data.getDictionaryName(i.getKey()), String.format("[ /%s/ ]", i.getValue()));
                else
                {
                    String num = IPATool.mergeFiveDegreeNum(i.getValue(), true);
                    String line = IPATool.mergeFiveDegree("", i.getValue(), false);
                    ipa.put(data.getDictionaryName(i.getKey()), String.format("[%-4s]%s", num, line));
                }
            }

            note = ListTool.mapping(
                    readJson(item.getNote(), new TypeReference<List<String>>() {}, new ObjectMapper()),
                    i -> ScTcText.get(i, l)
            );
        }
    }

    private List<Info> info;

    private PinyinDetail(String key, Dialect d, Language l)
    {
        this.key = key;

        // initial-b  = initial + b
        var arr = key.split("-");
        type = switch (arr[0])
        {
            case "initial" -> Type.INITIAL;
            case "last" -> Type.LAST;
            case "tone" -> Type.TONE;
            case "special" -> Type.SPECIAL;
            default -> throw new IllegalArgumentException("错误的格式" + arr[0]);
        };

        var ipaData = new IPAData(l, d, null);
        info = ListTool.mapping(IPAService.getTableItem(key), i -> new Info(i, ipaData));
    }

    public static Maybe<PinyinDetail> of(String key, Dialect d, Language l)
    {
        try
        {
            return Maybe.exist(new PinyinDetail(key, d, l));
        } catch (Exception e)
        {
            e.printStackTrace();
            return Maybe.nothing();
        }
    }
}
