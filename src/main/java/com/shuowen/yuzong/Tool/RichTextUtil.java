package com.shuowen.yuzong.Tool;

import com.shuowen.yuzong.Linguistics.Format.PinyinParam;
import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Mandarin.HanPinyin;
import com.shuowen.yuzong.Linguistics.Scheme.SPinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.error.InvalidPinyinException;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Scheme;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Twin;
import com.shuowen.yuzong.data.domain.IPA.IPAData;
import com.shuowen.yuzong.data.domain.IPA.IPAFormatter;
import com.shuowen.yuzong.Linguistics.Scheme.PinyinFormatter;
import com.shuowen.yuzong.data.domain.IPA.Phonogram;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinChecker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RichTextUtil
{
    private RichTextUtil()
    {
    }

    /**
     * 对于预览的拼音列做拼音初始化，只用于专业版
     *
     * @param s     字符串，里面只能有[]这个特殊字符
     * @param style 为数不多可以直接用style的流程
     */
    public static <U extends PinyinStyle> String format(String s, U style, Dialect d)
    {
        Matcher m = Pattern.compile("\\[[^\\]]+]").matcher(s);
        StringBuilder sb = new StringBuilder();

        while (m.find())
        {
            String content = m.group();
            var pyMaybe = d.tryCreatePinyin(SPinyin.of(content.substring(1, content.length() - 1)));
            String ans = pyMaybe.isValid() ?
                    PinyinFormatter.handle((pyMaybe.getValue()), style).toString() :
                    "{b 无效方言拼音}";
            m.appendReplacement(sb, Matcher.quoteReplacement(ans));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public static UString format(UString text, final IPAData data, boolean developer)
    {
        String s = text.toString();

        // 处理拼音类内容：[]
        var pattern = Pattern.compile("\\[[^]]+]");
        Matcher m = pattern.matcher(s);
        StringBuilder sb = new StringBuilder();


        while (m.find()) collectIPAShouldTransfer(m.group(), data);
        m = pattern.matcher(s);
        while (m.find()) m.appendReplacement(sb, Matcher.quoteReplacement(handlePinyinIPA(m.group(), data, developer)));
        m.appendTail(sb);
        s = sb.toString();

        s = handleAnnotation(s, true);

        s = s.replace("]  [", "] [");
        return UString.of(s);
        //TODO
        // 关键词绑定（包括隐藏绑定）
        // 链接：转换为<a>标签
        // 同音字表：特殊处理，关联多个汉字条目
    }

    private static void collectIPAShouldTransfer(String token, final IPAData data)
    {
        String content = token.substring(1, token.length() - 1);

        if (content.contains(" "))
        {
            if (content.startsWith("/") || content.startsWith("+") || content.startsWith("*"))
            {
                char prefix = content.charAt(0);
                content = content.substring(1);

                for (String part : content.trim().split("\\s+"))
                {
                    collectIPAShouldTransfer("[" + prefix + part + "]", data);
                }
            }
            else
            {
                for (String part : content.trim().split("\\s+"))
                {
                    collectIPAShouldTransfer("[" + part + "]", data);
                }
            }
            return;
        }

        if (content.startsWith("*"))
        {
            SPinyin pyText = SPinyin.of(content.substring(1));
            try
            {
                var py = data.getDialect().checkAndCreatePinyin(pyText);
                data.add(py);
            } catch (InvalidPinyinException ignored)
            {
            }
        }
    }

    /**
     * 处理所有和拼音有关的内容
     */
    private static String handlePinyinIPA(String token, final IPAData data, boolean developer)
    {
        // 去掉 [ ]
        String content = token.substring(1, token.length() - 1);

        // 支持空格分词，如 [lan4 cong1] / [+nan2 chang1]
        if (content.contains(" "))
        {
            StringBuilder result = new StringBuilder();
            if (content.startsWith("/") || content.startsWith("+") || content.startsWith("*"))
            {
                char prefix = content.charAt(0);
                content = content.substring(1);
                for (String part : content.trim().split("\\s+"))
                    result.append(handlePinyinIPA("[" + prefix + part + "]", data, developer));
            }
            else
            {
                for (String part : content.trim().split("\\s+"))
                    result.append(handlePinyinIPA("[" + part + "]", data, developer));
            }

            return result.toString();
        }

        Dialect d = data.getDialect();


        /* 包含"/"   "[/s]"      -> " [s] "
         * 包含"+"   "[+yi1]"    -> " [yī] "
         * 包含"*"   "[*cong1]"  -> " [ts'ɔŋ-˦˨] "
         * 包含"-"   ”[tsaŋ-42]“ -> " [tsaŋ-˦˨] "
         * 默认情况   "[ieu]"     -> " [iēu] "
         */
        if (content.startsWith("/"))
        {
            var latin = content.substring(1);
            return String.format(" [%s] ", latin);
        }
        else if (content.startsWith("+"))
        {
            var pyText = content.substring(1);
            try
            {
                // 以RPinyin为返回值的拼音已经拥有" [] "了
                return HanPinyin.of(pyText).getRead().toString();
            } catch (InvalidPinyinException e)
            {
                return String.format(" {b 无效汉语拼音：%b} ", pyText);
            }
        }
        else if (content.startsWith("*"))
        {
            SPinyin pyText = SPinyin.of(content.substring(1));
            try
            {
                // 创建拼音，如果失败，返回警告
                var py = d.checkAndCreatePinyin(pyText);

                // 创建音标，如果失败，返回警告
                var ipa = data.submitAndGet(py, d.getDefaultDict());
                if (ipa.isEmpty()) return " {b 无效国际音标} ";

                // 如果是开发者，应该看到两种内容
                if (developer)
                {
                    // RPinyin已经是 " [%s] "的格式了
                    // ipa已经是 "[内容]"的格式了，加上空格就可以了
                    return String.format(" %s （%s）",
                            ipa.getValue(),
                            PinyinFormatter.handle(py, d, PinyinParam.of(Scheme.STANDARD)).toString()
                    );
                }
                else
                {
                    return String.format(" %s ", ipa.getValue());
                }

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
        else if (content.contains("-"))
        {
            int idx = content.indexOf('-');
            var ipa = IPAFormatter.mergeFiveDegree(
                    content.substring(0, idx), content.substring(idx + 1), true);
            return String.format(" %s ", ipa); // ipa已经是 "[内容]"的格式了，加上空格就可以了
        }
        else
        {
            SPinyin pyText = SPinyin.of(content);
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

    /**
     * 当需要隐藏注释的时候，把那一部分替换成空字符串
     */
    private static String handleAnnotation(String text, boolean handle)
    {
        if (!handle) return text;

        StringBuilder result = new StringBuilder();
        String[] lines = text.split("\n", -1); // 保留空行

        boolean inBlockComment = false;

        for (String line : lines)
        {
            String trimmed = line.trim();

            // 进入多行注释
            if (!inBlockComment && trimmed.startsWith("/*"))
            {
                if (!trimmed.endsWith("*/")) inBlockComment = true;// 整行是注释 → 跳过（不输出）
                continue;
            }

            // 在多行注释中
            if (inBlockComment)
            {
                if (trimmed.endsWith("*/")) inBlockComment = false;// 整行都属于注释 → 跳过
                continue;
            }

            String processed = line.replaceAll("/\\*.*?\\*/", "");// 处理“行内注释”

            // 如果这一行删完是空的 → 说明原本是“只有注释”
            if (processed.trim().isEmpty() && !line.trim().isEmpty()) continue; // 这是“注释产生的空行” → 删除

            result.append(processed).append("\n"); // 正常行 or 原本空行 → 保留
        }

        return result.toString();
    }

    /**
     * 检查是否有无效拼音之类的内容
     */
    public static boolean checkWarning(UString text)
    {
        return !text.toString().contains(" {b 无效");
    }

    /**
     *
     */
    public static Twin<String> buildSnippet(
            String content, String query, int limit, Pattern pattern)
    {
        content = content.replace("\n", "");

        String prefix = "";
        String body = content;

        Matcher matcher = pattern.matcher(content);

        if (matcher.find() && matcher.start() == 0)
        {
            prefix = matcher.group();
            body = content.substring(prefix.length());
        }

        String lowerQuery = query.toLowerCase();

        String lowerPrefix = prefix.toLowerCase();
        int prefixIdx = lowerPrefix.indexOf(lowerQuery);

        if (prefixIdx != -1)
        {
            String highlightedPrefix =
                    prefix.substring(0, prefixIdx)
                            + "{b " + prefix.substring(prefixIdx, prefixIdx + query.length()) + "}"
                            + prefix.substring(prefixIdx + query.length());

            String snippet = StringTool.limitLength(body, limit, "……");

            return Twin.of(highlightedPrefix, snippet);
        }
        String lowerBody = body.toLowerCase();
        int idx = lowerBody.indexOf(lowerQuery);

        if (idx == -1)
        {
            return Twin.of(prefix, StringTool.limitLength(body, limit, "……"));
        }

        limit = Math.max(limit, query.length());

        int start = Math.max(0, idx - limit / 2);
        int end = Math.min(body.length(), start + limit);
        start = Math.max(0, end - limit);

        String snippet = body.substring(start, end);

        int rel = idx - start;

        snippet = snippet.substring(0, rel)
                + "{b " + snippet.substring(rel, rel + query.length()) + "}"
                + snippet.substring(rel + query.length());

        if (start > 0) snippet = "…… " + snippet;
        if (end < body.length()) snippet += " ……";

        return Twin.of(prefix, snippet);
    }

    public static UString handleRefTitle(UString text, Phonogram pho)
    {
        var content = text.toString();

        content = content.replaceAll("\\s?\\{\\+\\S+?}\\s?", " ");// 處理 {+xxx} → 刪除，並處理最多一個空格
        content = content.replaceAll("\\{-(\\S+?)}", "{b $1}"); // 處理 {-xxx} 和 {xxx} → {b xxx}
        content = content.replaceAll("\\{(?!b\\s)(\\S+?)}", "{b $1}");// {xxx}（避免重複處理已經是 {b xxx} 的情況）

        if (pho == Phonogram.PinyinIPA) content = content.replaceAll("\\[(?![+\\-*/])(.*?)]", "[*$1]");


        content = content.trim().replaceAll("\\s{2,}", " ");// 去掉多餘空格（可選）

        //  System.out.println(content);
        return UString.of(content);
    }
}
