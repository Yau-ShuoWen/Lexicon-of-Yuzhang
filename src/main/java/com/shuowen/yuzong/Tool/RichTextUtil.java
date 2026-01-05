package com.shuowen.yuzong.Tool;

import com.shuowen.yuzong.Linguistics.Format.PinyinParam;
import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Mandarin.HanPinyin;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Scheme;
import com.shuowen.yuzong.data.domain.IPA.IPAData;
import com.shuowen.yuzong.data.domain.IPA.IPATool;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinTool;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RichTextUtil
{
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
            var pyMaybe = d.tryCreatePinyin(content.substring(1, content.length() - 1));
            String ans = pyMaybe.isValid() ?
                    PinyinTool.formatPinyin((pyMaybe.getValue()), style) :
                    "{b 此处拼音无效，无法格式化}";
            m.appendReplacement(sb, Matcher.quoteReplacement(ans));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public static String format(String s, Dialect d, final IPAData data)
    {
        // 处理拼音类内容：[]
        Function<String, String> replaceToken = token ->
        {
            // 去掉 [ ]
            String content = token.substring(1, token.length() - 1);

            /* 包含"/"   "[/s]"      -> " [s] "
             * 包含"+"   "[+yi1]"    -> " [yī] "
             * 包含"*"   "[*cong1]"  -> " [ts'ɔŋ-˦˨] "
             * 包含"-"   ”[tsaŋ-42]“ -> " [tsaŋ-˦˨] "
             * 默认情况   "[ieu]"     -> " [iēu] "
             */
            if (content.startsWith("/")) return " [" + content.substring(1) + "] ";
            if (content.startsWith("+")) return " [" + HanPinyin.topMark(content.substring(1)) + "] ";
            if (content.startsWith("*"))
            {
                var pyMaybe = d.tryCreatePinyin(content.substring(1));
                return pyMaybe.isValid() ?
                        " " + data.getDirectly(pyMaybe.getValue(), d.getDefaultDict()) + " " :
                        "{b 此处拼音无效，无法转换国际音标}";
            }
            if (content.contains("-"))
            {
                int dashIndex = content.indexOf('-');
                String ans = IPATool.mergeFiveDegree(content.substring(0, dashIndex), content.substring(dashIndex + 1), true);
                return " [" + ans + "] ";
            }
            //  默认情况
            {
                var pyMaybe = d.tryCreatePinyin(content);
                return pyMaybe.isValid() ?
                        PinyinTool.formatPinyin(pyMaybe.getValue(), d, PinyinParam.of(Scheme.STANDARD)) :
                        "{b 此处拼音无效，无法格式化}";
            }
        };


        Matcher m = Pattern.compile("\\[[^\\]]+]").matcher(s);
        StringBuilder sb = new StringBuilder();

        while (m.find())
        {
            m.appendReplacement(sb, Matcher.quoteReplacement(replaceToken.apply(m.group())));
        }
        m.appendTail(sb);
        s = sb.toString();

        //TODO
        // 处理不显示的内容：/**/
        // s = s.replaceAll("/\\*.*?\\*/", "");
        // 处理特殊格式的内容：{}
        // 关键词绑定（包括隐藏绑定）
        // 链接：转换为<a>标签
        // 粗体：转换为<b>标签
        // 原文注释：转换为灰色小字
        // 同音字表：特殊处理，关联多个汉字条目

        return s.replace("]  [", "] [");
    }
}
