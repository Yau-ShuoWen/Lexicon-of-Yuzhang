package com.shuowen.yuzong.util.text;

import com.hankcs.hanlp.HanLP;
import com.shuowen.yuzong.util.core.Dialect;

import java.util.*;
import java.util.regex.Pattern;

public class Translate
{
    // 规则数据：方言只填写相对普通话增加或覆盖的部分。
    private record ContextData(String pattern, String origin, String target) {}
    private record RuleData(List<String> keep, Map<String, String> replace, List<ContextData> context) {}

    private static final RuleData commonData = new RuleData(
            List.of("「", "」"),
            Map.of("衹", "只", "祇", "只", "僊", "仙", "賸", "剩", "著", "着"),
            List.of(new ContextData(
                    "巨著|著名|著稱|顯著|卓著|昭著|著作|著書|著述|編著|撰著|名著|論著|譯著|專著|原著|合著|鉅著|拙著|土著|著雍|著書立說|彰明較著|見微知著",
                    "著", "著"))
    );

    private static final Map<String, RuleData> dialectData = Map.of(
            "lac", new RuleData(
                    List.of("箇", "嗰", "喫", "睏"),
                    Map.of("託", "托"),
                    List.of(new ContextData("談託|搭詻託", "託", "讬")))
    );

    // 统一构建规则；方言规则继承普通话，遇到同一字时方言配置覆盖。
    private record ContextRule(Pattern context, UChar origin, UChar target) {}
    private record Rules(Set<UChar> keep, Map<UChar, UChar> replace, List<ContextRule> context) {}

    private static Rules buildRules(Rules base, RuleData data)
    {
        Set<UChar> keep = new HashSet<>();
        Map<UChar, UChar> replace = new HashMap<>();
        List<ContextRule> context = new ArrayList<>();
        if (base != null)
        {
            keep.addAll(base.keep());
            replace.putAll(base.replace());
        }
        data.replace().forEach((from, to) ->
        {
            UChar original = UChar.of(from);
            keep.remove(original);
            replace.put(original, UChar.of(to));
        });
        for (String ch : data.keep()) keep.add(UChar.of(ch));
        for (var rule : data.context())
            context.add(new ContextRule(Pattern.compile(rule.pattern()), UChar.of(rule.origin()), UChar.of(rule.target())));
        if (base != null) context.addAll(base.context());
        return new Rules(Set.copyOf(keep), Map.copyOf(replace), List.copyOf(context));
    }

    private static final Rules commonRules = buildRules(null, commonData);
    private static final Map<String, Rules> dialectRules = buildDialectRules();

    private static Map<String, Rules> buildDialectRules()
    {
        Map<String, Rules> rules = new HashMap<>();
        dialectData.forEach((code, data) -> rules.put(code, buildRules(commonRules, data)));
        return Map.copyOf(rules);
    }

    private static UChar[] contextTargets(UString text, Rules rules)
    {
        UChar[] targets = new UChar[text.length()];
        String source = text.toString();
        for (var rule : rules.context())
        {
            var matcher = rule.context().matcher(source);
            while (matcher.find())
            {
                int index = source.codePointCount(0, matcher.start());
                for (int ch : matcher.group().codePoints().toArray())
                {
                    if (targets[index] == null && rule.origin().codePoint() == ch)
                        targets[index] = rule.target();
                    index++;
                }
            }
        }
        return targets;
    }

    private static void appendConverted(UString result, UString part)
    {
        if (part.isEmpty()) return;
        result.append(HanLP.t2s(part.toString()));
        part.clear();
    }

    public static UString toSc(UString text)
    {
        return toSc(text, null);
    }

    public static UString toSc(UString text, Dialect dialect)
    {
        Rules rules = dialect == null ? commonRules : dialectRules.getOrDefault(dialect.toString(), commonRules);
        var result = UString.of();
        var part = UString.of();
        UChar[] contextTargets = contextTargets(text, rules);
        int index = 0;
        for (var ch : text)
        {
            boolean keep = rules.keep().contains(ch);
            UChar replacement = keep ? ch : contextTargets[index] != null ?
                    contextTargets[index] : rules.replace().get(ch);
            if (replacement != null)
            {
                appendConverted(result, part);
                result.append(replacement);
            }
            else part.append(ch);
            index++;
        }
        appendConverted(result, part);
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
