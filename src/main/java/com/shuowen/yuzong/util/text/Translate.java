package com.shuowen.yuzong.util.text;

import com.hankcs.hanlp.HanLP;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.ext.list.ListTool;

import java.util.List;
import java.util.regex.Pattern;

public class Translate
{

    // 撤回规则按原文判断，避免 HanLP 转换后丢失原字（例如「」）。
    private static final List<UChar> immove = ListTool.mapping(new String[]{"「", "」"}, UChar::of);
    // 补充规则按转换结果判断，两表按相同下标对应。
    private static final List<UChar> origin = ListTool.mapping(new String[]{"衹", "祇", "僊", "賸"}, UChar::of);
    private static final List<UChar> target = ListTool.mapping(new String[]{"只", "只", "仙", "剩"}, UChar::of);

    // 方言只配置相对普通话增加的规则。
    private static final List<UChar> immoveLAC = ListTool.mapping(new String[]{"箇", "嗰", "喫", "睏"}, UChar::of);
    private static final List<UChar> originLAC = ListTool.mapping(new String[]{"託"}, UChar::of);
    private static final List<UChar> targetLAC = ListTool.mapping(new String[]{"讬"}, UChar::of);

    private static final Pattern zhu = Pattern.compile("巨著|著名|著稱|顯著|卓著|昭著|著作|著書|著述|編著|撰著|名著|論著|譯著|專著|原著|合著|鉅著|拙著|土著|著雍|著書立說|彰明較著|見微知著|著");

    private static boolean contains(List<UChar> rules, UChar ch)
    {
        for (var rule : rules)
            if (rule.contentEquals(ch)) return true;
        return false;
    }

    private static UString convertPart(UString text, Dialect dialect)
    {
        if (text.isEmpty()) return UString.of();

        var converted = UString.of(HanLP.t2s(text.toString()));
        var result = UString.of();
        for (var ch : converted)
        {
            boolean replaced = false;
            // 方言补充规则覆盖普通话的同一原字。
            if (dialect == Dialect.LAC)
            {
                for (int i = 0; i < originLAC.size(); i++)
                    if (originLAC.get(i).contentEquals(ch))
                    {
                        result.append(targetLAC.get(i));
                        replaced = true;
                        break;
                    }
            }
            if (replaced) continue;
            for (int i = 0; i < origin.size(); i++)
                if (origin.get(i).contentEquals(ch))
                {
                    result.append(target.get(i));
                    replaced = true;
                    break;
                }
            if (!replaced) result.append(ch);
        }

        return UString.of(zhu.matcher(result.toString()).replaceAll(m ->
                m.group().equals("著") ? "着" : m.group()));
    }

    public static UString toSc(UString text)
    {
        return toSc(text, null);
    }

    public static UString toSc(UString text, Dialect dialect)
    {
        var result = UString.of();
        var part = UString.of();
        for (var ch : text)
        {
            if (contains(immove, ch) || (dialect == Dialect.LAC && contains(immoveLAC, ch)))
            {
                result.append(convertPart(part, dialect));
                part.clear();
                result.append(ch);
            }
            else part.append(ch);
        }
        result.append(convertPart(part, dialect));
        return result;
    }

    public static String toSc(String text)
    {
        return toSc(UString.of(text)).toString();
    }

    public static String toSc(String text, Dialect dialect)
    {
        return toSc(UString.of(text), dialect).toString();
    }

    /**
     * 只有繁转简做了特调，剩余的部分直接调用
     */
    public static String handle(String text, String from, String to)
    {
        return switch (from + "->" + to)
        {
            case "sc->tc" -> HanLP.s2t(text);
            case "sc->tw" -> HanLP.s2tw(text);
            case "sc->hk" -> HanLP.s2hk(text);
            case "tc->sc" -> toSc(text);
            case "tc->tw" -> HanLP.t2tw(text);
            case "tc->hk" -> HanLP.t2hk(text);
            case "tw->sc" -> HanLP.tw2s(text);
            case "tw->tc" -> HanLP.tw2t(text);
            case "tw->hk" -> HanLP.tw2hk(text);
            case "hk->sc" -> HanLP.hk2s(text);
            case "hk->tc" -> HanLP.hk2t(text);
            case "hk->tw" -> HanLP.hk2tw(text);
            default -> throw new IllegalArgumentException("");
        };
    }
}
