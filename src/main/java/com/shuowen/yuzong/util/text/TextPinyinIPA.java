package com.shuowen.yuzong.util.text;

import com.shuowen.yuzong.dict.data.domain.IPA.IPA;
import com.shuowen.yuzong.dict.data.domain.IPA.IPAFormatter;
import com.shuowen.yuzong.dict.data.domain.Reference.DictCode;
import com.shuowen.yuzong.dict.data.domain.Reference.DictGroup;
import com.shuowen.yuzong.dict.pinyin.data.PinyinConfig;
import com.shuowen.yuzong.linguistics.Mandarin.HanPinyin;
import com.shuowen.yuzong.linguistics.pinyinv2.DictionaryRegistry;
import com.shuowen.yuzong.linguistics.util.PinyinBlock;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.err.InvalidPinyinException;
import com.shuowen.yuzong.util.tuple.Maybe;

import java.util.function.Function;
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

        // [-kɔŋ42]
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
        return format(text, data, developer, dict, isfromDB, false);
    }

    /** 仅供拼音表的拼音转换使用，列出所有辞书的 IPA 版本。 */
    public static String formatAllDictionaryVersions(String text, PinyinConfig data)
    {
        return format(text, data, false, Maybe.nothing(), false, true);
    }

    private static String format(String text, PinyinConfig data, boolean developer,
                                 Maybe<DictCode> dict, boolean isfromDB, boolean allDictionaryVersions)
    {
        Matcher m = pattern.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (m.find())
        {
            var handleAns = handle(normalize(m.group()), data, developer, dict, isfromDB, allDictionaryVersions);
            var replace = Matcher.quoteReplacement(handleAns);
            m.appendReplacement(sb, replace);
        }
        m.appendTail(sb);

        return sb.toString();//.replace("]  [", "] [");//作用就是化学实验前后都要洗一次，但是一定有一次是多余的
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
            boolean developer, Maybe<DictCode> dict, boolean isfromDB,
            boolean allDictionaryVersions)
    {
        Language l = data.getLanguage();

        if (pinyin.type == PinyinType.DIALECT)
        {
            return handle(pinyin.body, data, developer, dict, isfromDB, allDictionaryVersions).toTheString(l);
        }

        if (pinyin.body.contains(" "))
        {
            var str = new StringBuilder();
            for (String s : pinyin.body.trim().split("\\s+"))
            {
                var smaller = new PinyinToken(pinyin.type, s);
                str.append(handle(smaller, data, developer, dict, isfromDB, allDictionaryVersions));
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
                        var ipa = dict.isEmpty()
                                ? data.searchDefaultIPA(py)
                                : data.searchIPA(py, dict.getValue());
                        return ipa.getValue().toString();
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
                var ipa = IPAFormatter.mergeFiveDegree(pinyin.body);
                return String.format(" %s ", new IPA(ipa));
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
                                handle(new PinyinToken(PinyinType.OUT_IPA, ipa), data, true, dict, isfromDB, allDictionaryVersions),
                                handle(new PinyinToken(PinyinType.IGNORE, py), data, true, dict, isfromDB, allDictionaryVersions)
                        );
                    }
                    else
                    {
                        return handle(new PinyinToken(PinyinType.IGNORE, py), data, false, dict, isfromDB, allDictionaryVersions);
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

    private static PinyinBlock handle(
            String pyText, PinyinConfig data,
            boolean developer, Maybe<DictCode> dict, boolean isfromDB,
            boolean allDictionaryVersions
    )
    {
        Dialect d = data.getDialect();
        var dictGroup = DictGroup.of(d);

        if (pyText.contains(" "))
        {
            PinyinBlock block = new PinyinBlock();
            for (var i : pyText.trim().split("\\s+"))
            {
                block.merge(handle(i, data, developer, dict, isfromDB, allDictionaryVersions));
            }
            return block;
        }

        try
        {
            var py = isfromDB ? d.trustedCreatePinyin(pyText) : d.checkAndCreatePinyin(pyText);

            if (developer)
            {
                var block = py.format();
                var defaultIPA = data.searchDefaultIPA(py);
                block.add(
                        "yzcIPA",
                        "本詞典使用 IPA",
                        defaultIPA.handleIfExistAndGet(Object::toString, "—")
                );

                if (!dict.isEmpty()) // 没有字典上下文，就是标准模式，不处理
                {
                    addDictionaryVersions(block, py, data, dictGroup, dict.getValue(), true, false);
                }
                return block;
            }
            else
            {
                var block = py.format();
                var defaultIPA = data.searchDefaultIPA(py);
                block.add(
                        "yzcIPA",
                        "本詞典使用 IPA",
                        defaultIPA.handleIfExistAndGet(Object::toString, "—")
                );

                if (allDictionaryVersions)
                {
                    for (var i : dictGroup.getKeySet())
                        addDictionaryVersions(block, py, data, dictGroup, i, false, true);
                }
                else if (!dict.isEmpty())
                    addDictionaryVersions(block, py, data, dictGroup, dict.getValue(), true, false);

                return block;
            }
        } catch (InvalidPinyinException | IllegalArgumentException e)
        {
            return invalidPinyinBlock(developer, dict, dictGroup, allDictionaryVersions);
        }
    }

    private static PinyinBlock invalidPinyinBlock(
            boolean developer,
            Maybe<DictCode> dict,
            DictGroup dictGroup,
            boolean allDictionaryVersions
    )
    {
        PinyinBlock block = new PinyinBlock();
        block.setTitle("—");
        block.add("putonghua", "讀起來像", "—");
        block.add("standard", "標準寫法", "—");
        block.add("keyboard", "鍵盤輸入", "—");
        block.add("yzcIPA", "本詞典使用 IPA", "—");

        if (developer)
        {
            if (!dict.isEmpty())
                addDictionaryVersionPlaceholders(block, dictGroup, dict.getValue(), true);
        }
        else
        {
            if (allDictionaryVersions)
            {
                for (var dictionary : dictGroup.getKeySet())
                    addDictionaryVersionPlaceholders(block, dictGroup, dictionary, false);
            }
        }
        return block;
    }

    private static void addDictionaryVersionPlaceholders(
            PinyinBlock block,
            DictGroup dictGroup,
            DictCode dictionary,
            boolean useAsTitle
    )
    {
        addDictionaryVersionRows(block, dictGroup, dictionary, useAsTitle, true, ignored -> "—");
    }

    /**
     * 将一部中心辞书注册的全部 IPA 版本并列加入拼音块。
     * 中心版本沿用数据库中的辞书名，派生版本使用注册表中的版本名。
     */
    private static void addDictionaryVersions(
            PinyinBlock block,
            com.shuowen.yuzong.linguistics.pinyinv2.UniPinyin pinyin,
            PinyinConfig data,
            DictGroup dictGroup,
            DictCode dictionary,
            boolean useAsTitle,
            boolean includeMissing
    )
    {
        addDictionaryVersionRows(
                block,
                dictGroup,
                dictionary,
                useAsTitle,
                includeMissing,
                version -> data.searchIPA(pinyin, DictCode.of(version.toString()))
                        .handleIfExistAndGet(Object::toString, "—")
        );
    }

    private static void addDictionaryVersionRows(
            PinyinBlock block,
            DictGroup dictGroup,
            DictCode dictionary,
            boolean useAsTitle,
            boolean includeMissing,
            Function<com.shuowen.yuzong.linguistics.pinyinv2.DictCode, String> valueOf
    )
    {
        final com.shuowen.yuzong.linguistics.pinyinv2.DictCode central;
        try
        {
            central = com.shuowen.yuzong.linguistics.pinyinv2.DictCode.of(dictionary.toString())
                    .centralDictionary();
        }
        catch (IllegalArgumentException ignored)
        {
            return;
        }

        for (var version : DictionaryRegistry.ipaVersionsOf(central))
        {
            boolean centralVersion = version == central;
            String ipaText = valueOf.apply(version);
            if (!includeMissing && "—".equals(ipaText)) continue;
            String versionName = centralVersion
                    ? dictGroup.getName(DictCode.of(central.toString()), Language.TC)
                    : derivedVersionName(dictGroup, central, version);

            if (centralVersion && useAsTitle)
            {
                block.setTitle(ipaText);
                block.addFirst("dictIPA", versionName, ipaText);
            }
            else block.add("dictIPA", versionName, ipaText);
        }
    }

    private static String derivedVersionName(
            DictGroup dictGroup,
            com.shuowen.yuzong.linguistics.pinyinv2.DictCode central,
            com.shuowen.yuzong.linguistics.pinyinv2.DictCode version
    )
    {
        String dictionaryName = dictGroup.getName(DictCode.of(central.toString()), Language.TC);
        String centralName = central.getBookname().get(Language.TC).toString();
        String versionName = version.getBookname().get(Language.TC).toString();
        String suffix = versionName.substring(centralName.length())
                .replace('(', '（')
                .replace(')', '）');
        return dictionaryName + suffix;
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
