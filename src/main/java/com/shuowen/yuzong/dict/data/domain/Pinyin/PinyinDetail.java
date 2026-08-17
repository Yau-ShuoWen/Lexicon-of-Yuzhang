package com.shuowen.yuzong.dict.data.domain.Pinyin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.dict.data.domain.IPA.IPAFormatter;
import com.shuowen.yuzong.dict.data.domain.Reference.DictCodeExt;
import com.shuowen.yuzong.dict.data.model.IPA.IPAItem;
import com.shuowen.yuzong.dict.service.IPA.IPAService;
import com.shuowen.yuzong.util.ext.list.ListTool;
import com.shuowen.yuzong.util.text.ScTcText;
import com.shuowen.yuzong.util.text.UString;
import com.shuowen.yuzong.util.tuple.Maybe;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.shuowen.yuzong.util.json.JsonTool.readJson;

@Data
public class PinyinDetail
{
    private String key;

    private enum Type
    {INITIAL, LAST, TONE, SPECIAL, CHANGE}

    private Type type;

    @Getter
    private class Info
    {
        String key;
        String standard;
        TreeMap<String, String> ipa = new TreeMap<>();
        UString note;

        public Info(IPAItem item, final PinyinConfig data)
        {
            Language l = data.getLanguage();

            key = item.getUrl();
            standard = String.format("[%s]", item.getTitle());

            for (var i : readJson(item.getInfo(), new TypeReference<Map<DictCodeExt, String>>() {}).entrySet())
            {
                if ("-".equals(i.getValue())) continue;
                if (type != Type.TONE)
                    ipa.put(data.getDictName(i.getKey()), String.format("[ /%s/ ]", i.getValue()));
                else
                {
                    String num = IPAFormatter.mergeFiveDegreeNum(i.getValue(), true);
                    String line = IPAFormatter.mergeFiveDegree("", i.getValue(), false);
                    ipa.put(data.getDictName(i.getKey()), String.format("[%-4s][%s]", num, line));
                }
            }
            note = readJson(item.getNote(), new TypeReference<ScTcText>() {}).get(l);
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
            case "change" -> Type.CHANGE;
            default -> throw new IllegalArgumentException("错误的格式" + arr[0]);
        };

        var ipaData = new PinyinConfig(l, d);
        System.out.println(key);
        info = ListTool.mapping(IPAService.getTableItem(d, key), i -> new Info(i, ipaData));

        if (!info.isEmpty())
        {
            switch (d)
            {
                case LAC ->
                {
                    // 编码的历史原因，iung在ung前面，iuk在uk前面，所以需要手动重新调整顺序
                    if ("last-iung".equals(info.get(0).key) || "last-iuk".equals(info.get(0).key))
                        ListTool.swap(info, 0, 1);
                }
            }
        }

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
