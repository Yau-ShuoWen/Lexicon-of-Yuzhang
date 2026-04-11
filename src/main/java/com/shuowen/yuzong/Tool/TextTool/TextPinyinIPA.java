package com.shuowen.yuzong.Tool.TextTool;

import com.shuowen.yuzong.Linguistics.Format.PinyinParam;
import com.shuowen.yuzong.Linguistics.Mandarin.HanPinyin;
import com.shuowen.yuzong.Linguistics.Scheme.PinyinFormatter;
import com.shuowen.yuzong.Linguistics.Scheme.SPinyin;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.error.InvalidPinyinException;
import com.shuowen.yuzong.Tool.dataStructure.option.Scheme;
import com.shuowen.yuzong.data.domain.IPA.IPAData;
import com.shuowen.yuzong.data.domain.IPA.IPAFormatter;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinChecker;
import com.shuowen.yuzong.data.domain.Reference.DictCode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextPinyinIPA
{
    private TextPinyinIPA()
    {

    }

    private enum PinyinType
    {
        HAN, IGNORE, DIALECT, IPA, OUT_IPA, NOTHING
    }

    private static class PinyinToken
    {
        PinyinType type;
        String body;

        PinyinToken(PinyinType type, String body)
        {
            this.type = type;
            this.body = body;
        }
    }

    /**
     * 正規化
     */
    private static PinyinToken normalize(String content)
    {
        content = content.substring(1, content.length() - 1);

        if (content.isEmpty()) return new PinyinToken(PinyinType.NOTHING, "");

        // [/s]
        if (content.startsWith("/")) return new PinyinToken(PinyinType.IGNORE, content.substring(1));

        // [+yī]
        if (content.startsWith("+")) return new PinyinToken(PinyinType.HAN, content.substring(1));

        // [*gon1]
        if (content.startsWith("*")) return new PinyinToken(PinyinType.IPA, content.substring(1));

        // [kɔŋ-42]
        if (content.contains("-")) return new PinyinToken(PinyinType.OUT_IPA, content);

        // 默认：[gon1]
        return new PinyinToken(PinyinType.DIALECT, content);
    }

    /**
     * 工具类入口
     */
    public static String format(String text, final IPAData data, boolean developer, Maybe<DictCode> dict)
    {
        var pattern = Pattern.compile("\\[[^]]+]");

        // 预处理所有和拼音有关的内容插入，不一定会查询，但是：
        // 1. 只要不查询就没有性能消耗
        // 2. 只要查询就能把所有都找出来
        Matcher m = pattern.matcher(text);
        while (m.find()) collectPinyin(normalize(m.group()), data);

        // 构建
        StringBuilder sb = new StringBuilder();
        m = pattern.matcher(text);
        while (m.find())
        {
            var handleAns = handle(normalize(m.group()), data, developer, dict);
            var replace = Matcher.quoteReplacement(handleAns);
            m.appendReplacement(sb, replace);
        }
        m.appendTail(sb);

        return sb.toString().replace("]  [", "] [");
    }

    /**
     * 处理所有和拼音有关的内容
     *
     * @param pinyin    拼音片段
     * @param data      查询国际音标，附带上下文
     * @param developer 如果是开发者，可以看到
     * @param dict    字典的拼音要特殊处理
     */
    private static String handle(
            PinyinToken pinyin, final IPAData data,
            boolean developer, Maybe<DictCode> dict)
    {
        if (pinyin.body.contains(" "))
        {
            var result = new StringBuilder();
            for (String s : pinyin.body.split("\\s+"))
            {
                var smaller = new PinyinToken(pinyin.type, s);
                result.append(handle(smaller, data, developer, dict));
            }
            return result.toString();
        }

        var d = data.getDialect();

        switch (pinyin.type)
        {
            case IGNORE ->
            {
                return String.format(" [%s] ", pinyin.body);
            }
            case HAN ->
            {
                try
                {
                    return HanPinyin.of(pinyin.body).getRead().toString();
                } catch (InvalidPinyinException e)
                {
                    return String.format(" {b 无效汉语拼音：%b} ", pinyin.body);
                }
            }
            case IPA ->
            {
                SPinyin pyText = SPinyin.of(pinyin.body);
                try
                {
                    // 创建拼音，如果失败，返回警告
                    var py = d.checkAndCreatePinyin(pyText);

                    // 创建音标，如果失败，返回警告
                    var ipa = data.submitAndGet(py, dict.getValueOrDefault(d.getDefaultDict()));
                    if (ipa.isEmpty()) return " {b 无效国际音标} ";

                    return String.format(" %s ", ipa.getValue());

                } catch (InvalidPinyinException e)
                {
                    // 拼音异常，如果是编辑者模式下，提示文本长一些。如果是查看模式，直接报错即可。
                    if (developer)
                    {
                        var checkRes = PinyinChecker.suggestively(pyText, d);
                        return (checkRes.getLeft() == 2) ?
                                String.format(" {b 无效方言拼音：[%s]，是否应为：[%s]？}", pyText, checkRes.getRight()) :
                                String.format(" {b 无效方言拼音：[%s]} ", pyText);
                    }
                    else return " {b 无效方言拼音} ";
                }
            }
            case OUT_IPA ->
            {
                int idx = pinyin.body.indexOf('-');
                var ipa = IPAFormatter.mergeFiveDegree(
                        pinyin.body.substring(0, idx), pinyin.body.substring(idx + 1), true);
                return String.format(" %s ", ipa); // ipa已经是 "[内容]"的格式了，加上空格就可以了
            }
            case DIALECT ->
            {
                // 如果是在辞书里，这里的方言拼音不是简单的方言拼音。是辞典的特殊流程
                if (dict.isValid())
                {
                    // 如果是开发者模式，就显示两个
                    if (developer)
                    {
                        // developer 为 true 是因为这个分支里都是如此；dict 为空是因为拆分了之后就不用了
                        return String.format("%s/%s", handle(pinyin, data, true, Maybe.nothing()),
                                handle(new PinyinToken(PinyinType.IPA, pinyin.body), data, true, Maybe.nothing())
                        );
                    }
                    else
                    {
                        return switch (data.getPinyinOption().getPhonogram())
                        {
                            case AllPinyin -> handle(pinyin, data, false, Maybe.nothing());
                            case PinyinIPA -> handle(new PinyinToken(PinyinType.IPA, pinyin.body), data, false, Maybe.nothing());
                        };
                    }

                }
                else
                {
                    var pyText = SPinyin.of(pinyin.body);
                    try
                    {
                        var py = d.checkAndCreatePinyin(pyText);
                        // RPinyin已经是 " [%s] "的格式了
                        return PinyinFormatter.handle(py, d, PinyinParam.of(Scheme.STANDARD)).toString();
                    } catch (InvalidPinyinException e)
                    {
                        if (developer)
                        {
                            var checkRes = PinyinChecker.suggestively(pyText, d);
                            return (checkRes.getLeft() == 2) ?
                                    String.format(" {b 无效方言拼音：[%s]，是否应为：[%s]？}", pyText, checkRes.getRight()) :
                                    String.format(" {b 无效方言拼音：[%s]} ", pyText);
                        }
                        else return " {b 无效方言拼音} ";
                    }
                }
            }
            case NOTHING ->
            {
                if (developer) return "{b 这里有一个空的拼音标记}";
                else return "";
            }
            default -> throw new RuntimeException("不可到达的位置");
        }
    }

    private static void collectPinyin(PinyinToken pinyin, final IPAData data)
    {
        if (pinyin.body.contains(" "))
        {
            for (String s : pinyin.body.split("\\s+"))
            {
                var smaller = new PinyinToken(pinyin.type, s);
                collectPinyin(smaller, data);
            }
            return;
        }

        SPinyin pyText = SPinyin.of(pinyin.body);
        try
        {
            var py = data.getDialect().checkAndCreatePinyin(pyText);
            data.add(py);
        } catch (InvalidPinyinException ignored)
        {
        }
    }
}
