package com.shuowen.yuzong.Linguistics.Scheme;

import java.text.Normalizer;
import java.util.*;

public class ToneParser
{
    public static SPinyin parse(String input, Map<Character, String> toneMap, String defaultTone)
    {
        // Unicode 分解
        String nfd = Normalizer.normalize(input, Normalizer.Form.NFD);

        StringBuilder base = new StringBuilder();
        String tone = "";

        // 提取声调
        for (char c : nfd.toCharArray())
        {
            if (toneMap.containsKey(c)) tone = toneMap.get(c);
            else base.append(c);
        }

        // 合并回字符
        String result = Normalizer.normalize(base, Normalizer.Form.NFC);
        // 回退
        if (tone.isEmpty()) tone = defaultTone;

        return SPinyin.of(result + tone);
    }
}