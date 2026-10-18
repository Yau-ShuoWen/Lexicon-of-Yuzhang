package com.shuowen.yuzong.util.text;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** 单字转换：简体按大陆字形，繁体按台湾字形；不处理词语语境。 */
public final class ScTcCharacterConverter
{
    private static final Map<String, Map<UChar, List<UChar>>> SOURCES = ScTcCharacterData.sourceMappings();
    private static final Map<UChar, List<UChar>> ST = SOURCES.get("opencc-st");
    private static final Map<UChar, List<UChar>> TS = SOURCES.get("opencc-ts");
    private static final Map<UChar, List<UChar>> TW = SOURCES.get("opencc-tw-variants");
    private static final Map<UChar, List<UChar>> UNIHAN_SIMPLIFIED = SOURCES.get("unihan-simplified");
    private static final Map<UChar, List<UChar>> UNIHAN_TRADITIONAL = SOURCES.get("unihan-traditional");
    private static final Map<UChar, List<UChar>> REVERSE_ST = reverse(ST);
    private static final Map<UChar, List<UChar>> REVERSE_UNIHAN_TRADITIONAL = reverse(UNIHAN_TRADITIONAL);

    private ScTcCharacterConverter() {}

    /** 标准简体、台湾标准繁体和其他异体分别列出。一个字可能同时属于前两类。 */
    public record Forms(List<UChar> simplified, List<UChar> traditional, List<UChar> variants)
    {
        public Forms
        {
            simplified = List.copyOf(simplified);
            traditional = List.copyOf(traditional);
            variants = List.copyOf(variants);
        }
    }

    /** 简转繁结果，标准字和异体字分开，便于界面明确标注。 */
    public record TraditionalResult(List<UChar> standard, List<UChar> variants)
    {
        public TraditionalResult
        {
            standard = List.copyOf(standard);
            variants = List.copyOf(variants);
        }
    }

    public static Forms lookup(String character)
    {
        return lookup(UChar.of(character));
    }

    public static Forms lookup(UChar character)
    {
        Objects.requireNonNull(character, "character");
        LinkedHashSet<UChar> simplified = new LinkedHashSet<>(simplify(character));
        LinkedHashSet<UChar> traditional = new LinkedHashSet<>();
        for (UChar sc : simplified)
        {
            List<UChar> candidates = ST.get(sc);
            if (candidates == null) candidates = UNIHAN_TRADITIONAL.getOrDefault(sc, List.of(sc));
            for (UChar candidate : candidates)
                traditional.addAll(TW.getOrDefault(candidate, List.of(candidate)));
        }

        LinkedHashSet<UChar> variants = new LinkedHashSet<>(ScTcCharacterData.equivalents(character));
        variants.removeAll(simplified);
        variants.removeAll(traditional);
        return new Forms(List.copyOf(simplified), List.copyOf(traditional), List.copyOf(variants));
    }

    /** 任意字形繁转简；不经过词语转换。 */
    public static List<UChar> toSimplified(UChar character)
    {
        return lookup(character).simplified();
    }

    public static List<UChar> toSimplified(String character)
    {
        return toSimplified(UChar.of(character));
    }

    /** 简转台湾繁体，同时返回可显示为“异体”的其他字形。 */
    public static TraditionalResult toTraditional(UChar character)
    {
        Forms forms = lookup(character);
        return new TraditionalResult(forms.traditional(), forms.variants());
    }

    public static TraditionalResult toTraditional(String character)
    {
        return toTraditional(UChar.of(character));
    }

    private static List<UChar> simplify(UChar character)
    {
        List<UChar> values = TS.get(character);
        if (values != null) return values;
        values = UNIHAN_SIMPLIFIED.get(character);
        if (values != null) return values;
        if (ST.containsKey(character) || UNIHAN_TRADITIONAL.containsKey(character)) return List.of(character);
        values = REVERSE_ST.get(character);
        if (values != null) return values;
        return REVERSE_UNIHAN_TRADITIONAL.getOrDefault(character, List.of(character));
    }

    private static Map<UChar, List<UChar>> reverse(Map<UChar, List<UChar>> source)
    {
        Map<UChar, LinkedHashSet<UChar>> reversed = new LinkedHashMap<>();
        source.forEach((key, values) -> {
            for (UChar value : values)
                reversed.computeIfAbsent(value, ignored -> new LinkedHashSet<>()).add(key);
        });
        Map<UChar, List<UChar>> result = new LinkedHashMap<>();
        reversed.forEach((key, values) -> result.put(key, List.copyOf(values)));
        return Collections.unmodifiableMap(result);
    }
}
