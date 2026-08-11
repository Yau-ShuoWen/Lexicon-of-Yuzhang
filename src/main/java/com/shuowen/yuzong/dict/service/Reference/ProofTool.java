package com.shuowen.yuzong.dict.service.Reference;

import com.shuowen.yuzong.dict.data.domain.Reference.DictCode;

public class ProofTool
{
    public static String proof(DictCode c, String s)
    {
        return switch (c.getCode())
        {
            case "ncdict" ->
            {
                s = handleSerial(s);
                yield  s
                        .replace(",", "，")
                        .replace(";", "；")
                        .replace("?", "？")
                        .replace("!", "！")
                        .replace("~", "～")
                        .replace(":", "：")
                        .replace("(", "（")
                        .replace(")", "）")
                        .replace("話說", "話{n 説}")
                        .replace("冇", "冒")
                        .replace("什咩", "什哩")
                        .replace("", "");
            }
            default -> s;
        };
    }

    private static String handleSerial(String s)
    {
        return s.
                replace("❶", "①").
                replace("❷", "②").
                replace("❸", "③").
                replace("❹", "④").
                replace("❺", "⑤").
                replace("❻", "⑥").
                replace("❼", "⑦").
                replace("❽", "⑧").
                replace("❾", "⑨").
                replace("❿", "⑩")
                ;
    }
}