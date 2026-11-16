package com.shuowen.yuzong.dict.hanzi.domain;

import com.shuowen.yuzong.linguistics.Mandarin.HanPinyin;
import com.shuowen.yuzong.util.text.UString;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/** 将编辑汉字列表的查询文本翻译为明确的查询条件。 */
public record HanziEditorQuery(List<String> characters, String syllable, Integer tone)
{
    private static final Pattern PINYIN = Pattern.compile("[a-züv]+[0-5]?");
    private static final Pattern COMMAND = Pattern.compile("^([a-zA-Z]+)\\s+(.+)$");

    public static HanziEditorQuery parse(String input)
    {
        String text = input == null ? "" : input.trim();
        if (text.isEmpty()) return new HanziEditorQuery(List.of(), null, null);

        var command = COMMAND.matcher(text);
        if (command.matches())
        {
            if (!command.group(1).equalsIgnoreCase("py"))
                throw new IllegalArgumentException("不支持的查询码：" + command.group(1));
            return parseMandarinPinyin(command.group(2).trim());
        }
        if (text.equalsIgnoreCase("py")) throw new IllegalArgumentException("请输入汉语拼音，例如 py san1");

        var characters = new LinkedHashSet<String>();
        for (var character : UString.of(text).chars()) characters.add(character);
        return new HanziEditorQuery(new ArrayList<>(characters), null, null);
    }

    private static HanziEditorQuery parseMandarinPinyin(String value)
    {
        String normalized = value.toLowerCase(Locale.ROOT);
        if (!PINYIN.matcher(normalized).matches())
            throw new IllegalArgumentException("拼音查询格式应为 py san1 或 py san");
        boolean hasTone = Character.isDigit(normalized.charAt(normalized.length() - 1));
        int tone = hasTone ? normalized.charAt(normalized.length() - 1) - '0' : 0;
        if (tone == 5) tone = 0;
        String syllable = hasTone ? normalized.substring(0, normalized.length() - 1) : normalized;
        var pinyin = HanPinyin.of(hasTone ? syllable + tone : syllable);
        return new HanziEditorQuery(List.of(), pinyin.getSyll(), hasTone ? tone : null);
    }
}
