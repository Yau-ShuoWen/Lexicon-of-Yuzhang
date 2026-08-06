package com.shuowen.yuzong.linguistics.util;

import java.text.Normalizer;
import java.util.Map;

public class ToneParser
{
    public static KeyboardPinyin parse(String input, Map<Character, String> toneMap)
    {
        String nfd = Normalizer.normalize(input, Normalizer.Form.NFD); // 分解

        StringBuilder base = new StringBuilder();
        String tone = "";

        for (char c : nfd.toCharArray()) // 提取声调
        {
            if (toneMap.containsKey(c)) tone = toneMap.get(c);
            else base.append(c);
        }
        String result = Normalizer.normalize(base, Normalizer.Form.NFC); // 合并
        return KeyboardPinyin.of(result + tone);
    }
}