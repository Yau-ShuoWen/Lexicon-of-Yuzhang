package com.shuowen.yuzong.Tool;

import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Scheme.SPinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import com.shuowen.yuzong.Tool.TextTool.TextPinyinIPA;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Twin;
import com.shuowen.yuzong.data.domain.IPA.IPAData;
import com.shuowen.yuzong.Linguistics.Scheme.PinyinFormatter;
import com.shuowen.yuzong.data.domain.Reference.DictCode;

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
        Matcher m = Pattern.compile("\\[[^]]+]").matcher(s);
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

    /**
     * @param text      需要处理的字段
     * @param data      国际音标查询类
     * @param developer 开发者模式
     * @param dict      可能有也可能没有的字典代号
     * @param isfromDB  是否是从数据库的来的数据
     */
    public static UString format
    (UString text, final IPAData data, boolean developer, Maybe<DictCode> dict, boolean isfromDB)
    {
        String s = text.toString();

        s = TextPinyinIPA.format(s, data, developer, dict, isfromDB);
        s = handleAnnotation(s);

        return UString.of(s);
        //TODO
        // 关键词绑定（包括隐藏绑定）
        // 链接：转换为<a>标签
        // 同音字表：特殊处理，关联多个汉字条目
    }

    public static UString easyFormatFromTc(String text, final IPAData data)
    {
        return RichTextUtil.format(ScTcText.get(
                text, data.getDialect(), data.getLanguage()
        ), data, false, Maybe.nothing(), true);
    }

    /**
     * 当需要隐藏注释的时候，把那一部分替换成空字符串
     */
    private static String handleAnnotation(String text)
    {
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
     * 检查是否有无效拼音之类的内容<br>
     * 目前的简化方法就是检测句子中是否有{@code " {b 无效"}
     */
    public static boolean checkWarning(UString text)
    {
        return !text.toString().contains("【无效");
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

    public static UString handleRefTitle(UString text)
    {
        var content = text.toString();

        content = content.replaceAll("\\s?\\{\\+\\S+?}\\s?", " ");// 處理 {+xxx} → 刪除，並處理最多一個空格
        content = content.replaceAll("\\{-(\\S+?)}", "{b $1}"); // 處理 {-xxx} 和 {xxx} → {b xxx}
        content = content.replaceAll("\\{(?!b\\s)(\\S+?)}", "{b $1}");// {xxx}（避免重複處理已經是 {b xxx} 的情況）

        content = content.trim().replaceAll("[ \\t]{2,}", " ");// 去掉多餘空格（可選）
        return UString.of(content);
    }
}
