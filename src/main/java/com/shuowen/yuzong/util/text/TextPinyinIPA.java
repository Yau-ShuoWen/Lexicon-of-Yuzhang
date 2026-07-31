package com.shuowen.yuzong.util.text;

import com.shuowen.yuzong.Linguistics.Mandarin.HanPinyin;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.data.domain.IPA.IPAFormatter;
import com.shuowen.yuzong.data.domain.IPA.PinyinMode;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinConfig;
import com.shuowen.yuzong.data.domain.Reference.DictCode;
import com.shuowen.yuzong.data.domain.Reference.DictGroup;
import com.shuowen.yuzong.util.err.InvalidPinyinException;
import com.shuowen.yuzong.util.tuple.Maybe;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextPinyinIPA
{
    private TextPinyinIPA()
    {

    }

    // 汉语拼音
    private enum PinyinType
    {
        HAN,      // 汉语拼音
        IGNORE,   // 拉丁字母，只包装不修改
        DIALECT,  // 方言拼音
        IPA,      // 音系内部国际音标
        OUT_IPA,  // 音系外部国际音标
        CUSTOM,   // 自定义国际音标
        NOTHING   // 空拼音保护，只删除不修改
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
     * 正则化
     */
    private static PinyinToken normalize(String content)
    {
        content = content.substring(1, content.length() - 1);

        if (content.trim().isEmpty()) return new PinyinToken(PinyinType.NOTHING, "");

        // [/s]
        if (content.startsWith("/")) return new PinyinToken(PinyinType.IGNORE, content.substring(1));

        // [+yī]
        if (content.startsWith("+")) return new PinyinToken(PinyinType.HAN, content.substring(1));

        // [*gon1]
        if (content.startsWith("*")) return new PinyinToken(PinyinType.IPA, content.substring(1));

        // [-kɔŋ_42]
        if (content.startsWith("-")) return new PinyinToken(PinyinType.OUT_IPA, content.substring(1));

        //
        if (content.startsWith("#")) return new PinyinToken(PinyinType.CUSTOM, content.substring(1));

        // 默认：[gon1]
        return new PinyinToken(PinyinType.DIALECT, content);
    }

    private static final Pattern pattern = Pattern.compile("\\[[^]]+]");

    /**
     * 工具类入口：格式化内容
     */
    public static String format(String text, PinyinConfig data, boolean developer, Maybe<DictCode> dict, boolean isfromDB)
    {
        Matcher m = pattern.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (m.find())
        {
            var handleAns = handle(normalize(m.group()), data, developer, dict, isfromDB);
            var replace = Matcher.quoteReplacement(handleAns);
            m.appendReplacement(sb, replace);
        }
        m.appendTail(sb);

        return sb.toString().replace("]  [", "] [");//作用就是化学实验前后都要洗一次，但是一定有一次是多余的
    }

    /**
     * 处理所有和拼音有关的内容
     *
     * @param pinyin    拼音片段
     * @param data      查询国际音标，附带上下文
     * @param developer 如果是开发者，可以看到
     * @param dict      字典的拼音要特殊处理
     */
    private static String handle(
            PinyinToken pinyin, PinyinConfig data,
            boolean developer, Maybe<DictCode> dict, boolean isfromDB)
    {
        Language l = data.getLanguage();
        if (pinyin.body.contains(" "))
        {
            var str = new StringBuilder();
            for (String s : pinyin.body.trim().split("\\s+"))
            {
                var smaller = new PinyinToken(pinyin.type, s);
                str.append(handle(smaller, data, developer, dict, isfromDB));
            }
            return str.toString();
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
                    return String.format(" 【无效汉语拼音：%b】 ", pinyin.body);
                }
            }
            case IPA ->
            {
                var pyText = pinyin.body;
                try
                {
                    // 创建拼音，如果失败，返回警告
                    var py = isfromDB ?
                            d.trustedCreatePinyin(pyText) :
                            d.checkAndCreatePinyin(pyText);

                    // 创建音标，如果失败，返回警告
                    try
                    {
                        return data.searchIPA(py, dict.getValueOrDefault(d.getDefaultDict())).getValue();
                    } catch (Exception e)
                    {
                        return " 【无效国际音标】 ";
                    }

                } catch (InvalidPinyinException e)
                {
                    return developer ? String.format(" 【无效方言拼音：[%s]】 ", pyText) : " 【无效方言拼音】 ";
                }
            }
            case OUT_IPA ->
            {
                int idx = pinyin.body.indexOf('_');
                var ipa = IPAFormatter.mergeFiveDegree(
                        pinyin.body.substring(0, idx), pinyin.body.substring(idx + 1), false);
                return String.format(" [%s] ", ipa); // ipa不是是 "[内容]"的格式
            }
            case DIALECT ->
            {
                try
                {
                    var pyText = pinyin.body;
                    var py = isfromDB ? d.trustedCreatePinyin(pyText) : d.checkAndCreatePinyin(pyText);
                    var dictGroup = DictGroup.of(d);

                    if (developer)
                    {
                        var block = py.format(PinyinMode.STANDARD);

                        if (!dict.isEmpty()) // 没有字典上下文，就是标准模式，不处理
                        {
                            var ipa = data.searchIPA(py, dict.getValue()).getValueOrDefault("？");
                            String dictName = dictGroup.getName(dict.getValue(), Language.TC);

                            block.setTitle(ipa);
                            block.addFirst(dictName, ipa);
                        }
                        return block.toTheString(l);
                    }
                    else
                    {
                        var block = py.format(data.getPinyinMode());

                        switch (data.getPinyinMode())
                        {
                            case STANDARD ->
                            {
                                var dictCode = d.getDefaultDict();
                                var ipa = data.searchIPA(py, dictCode);
                                String dictName = DictGroup.of(d).getName(dictCode, Language.TC);

                                if (ipa.isValid())
                                {
                                    // 标准版的普通拼音，如果有拼音，加这个，但是放在最后
                                    if (dict.isEmpty()) block.add(dictName, ipa.getValue());
                                        // 标准版的字典拼音，如果有拼音，放在最前面和标题
                                    else block.addFirst(dictName, ipa.getValue());
                                }
                            }
                            // 专业版把国际音标放在最前面，并且放在首位
                            case PROFESSIONAL ->
                            {
                                if (dict.isEmpty())
                                {
                                    for (var i : dictGroup.getKeySet())
                                    {
                                        var ipa = data.searchIPA(py, i);
                                        String dictName = dictGroup.getName(i, Language.TC);
                                        if (ipa.isValid()) block.add(dictName, ipa.getValue());
                                    }
                                }
                                else
                                {
                                    for (var i : dictGroup.getKeySet())
                                    {
                                        var ipa = data.searchIPA(py, i);
                                        String dictName = dictGroup.getName(i, Language.TC);
                                        if (ipa.isValid())
                                        {
                                            block.add(dictName, ipa.getValue());
                                            if (i.equals(dict.getValue()))
                                                block.setTitle(ipa.getValue());
                                        }
                                    }
                                }
                            }
                            // 初学者在哪，叫一声，有回应吗？
                        }

                        return String.format(block.toTheString(l));
                    }
                } catch (InvalidPinyinException e)
                {
                    return ScTcText.get("【無效拼音】", "【无效拼音】", l);
                }
            }
            case CUSTOM ->
            {
                try
                {
                    int idx = pinyin.body.indexOf('=');
                    var ipa = pinyin.body.substring(0, idx);
                    var py = pinyin.body.substring(idx + 1);
                    if (developer)
                    {
                        return String.format("%s/%s",
                                handle(new PinyinToken(PinyinType.OUT_IPA, ipa), data, true, dict, isfromDB),
                                handle(new PinyinToken(PinyinType.IGNORE, py), data, true, dict, isfromDB)
                        );
                    }
                    else
                    {
                        return switch (data.getPinyinMode())
                        {
                            //DOTO
                            case INTRODUCE, STANDARD ->
                                    handle(new PinyinToken(PinyinType.IGNORE, py), data, false, dict, isfromDB);
                            case PROFESSIONAL ->
                                    handle(new PinyinToken(PinyinType.OUT_IPA, ipa), data, false, dict, isfromDB);
                        };
                    }
                } catch (StringIndexOutOfBoundsException e)
                {
                    return "{b 井號拼音格式錯誤}";
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

    /**
     * 工具类入口：把展示和存储的内容相互转化
     */
    public static String transferPinyin(String text, Dialect d, boolean isfromDB)
    {
        StringBuilder sb = new StringBuilder();
        Matcher m = pattern.matcher(text);
        while (m.find())
        {
            var token = normalize(m.group());
            var handleAns = token.type == PinyinType.DIALECT ?
                    pinyinToDB(token, d, isfromDB) :
                    m.group();
            var replace = Matcher.quoteReplacement(handleAns);
            m.appendReplacement(sb, replace);
        }
        m.appendTail(sb);

        return sb.toString();
    }

    private static String pinyinToDB(PinyinToken pinyin, Dialect d, boolean isfromDB)
    {
        // 保证是PinyinType.DIALECT

        if (pinyin.body.contains(" "))
        {
            List<String> list = new ArrayList<>();
            var str = new StringBuilder();
            for (String s : pinyin.body.split("\\s+"))
            {
                var smaller = new PinyinToken(pinyin.type, s);
                str.append(pinyinToDB(smaller, d, isfromDB));
            }
            return str.toString().replace("][", " ");
        }

        if (isfromDB)
        {
            return "[" + d.trustedCreatePinyin(pinyin.body).toKeyboardPinyin().toString() + "]";
        }
        else
        {
            return d.checkAndCreatePinyin(pinyin.body).toDatabasePinyin().toString(false);
        }
    }
}
