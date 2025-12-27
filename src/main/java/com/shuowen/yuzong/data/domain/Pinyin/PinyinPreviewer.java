package com.shuowen.yuzong.data.domain.Pinyin;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Scheme.NamPinyin;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;

import static com.shuowen.yuzong.data.domain.Pinyin.PinyinTool.parseAndReplace;

public class PinyinPreviewer
{
    public static String getPreview(PinyinStyle style, Dialect d)
    {
        return switch (d)
        {
            case NAM -> previewNam(d.castStyle(style));
        };
    }

    private static String previewNam(NamStyle style)
    {
        String s1 = "枫桥夜泊\n月落乌啼霜满天，江枫渔火对愁眠。\n姑苏城外寒山寺，夜半钟声到客船。\n";
        String p1 = """
                        [fung1][qieu2][ia5][pok6]
                        [nyuot6][lok7][u1][ti2][song1][mon3][tien1]
                        [gong1][fung1][yu4][fo3][dui4][ceu2][mien4]
                        [gu1][su1][ceen2][uai5][hon2][san1][sii5]
                        [ia5][bon4][zung1][sen1][tau5][kak6][cuon2]
                        """;

        String s2 = "山行\n远上寒山石径斜，白云生处有人家。\n停车坐爱枫林晚，霜叶红于二月花。\n";
        String p2 = """
                        [san1][xin4]
                        [yuon2][song5][hon2][san1][sak7][jin4][xia4]
                        [pak6][yun4][sang1][cu5][iu3][nin4][ga1]
                        [tiang2][ca1][co5][ngai4][fung1][lin4][uon3]
                        [suong1][iet6][fung4][yu4][oe5][nyuot6][fa1]
                        """;

        String s3 = "江雪\n千山鸟飞绝，万径人踪灭。\n孤舟蓑笠翁，独钓寒江雪。\n";
        String p3 = """
                        [gong1][xyuot6]
                        [qien1][san1][nieu3][feei1][jyuot6]
                        [uon5][jin4][nin4][zung1][miet6]
                        [gu1][zeu1][suo1][lit7][ung4]
                        [tuk7][dieu4][hon2][gong1][xyuot6]
                        """;

        return s1 + parseAndReplace(p1, NamPinyin::new, style, "[", "]") + "\n" +
                s2 + parseAndReplace(p2, NamPinyin::new, style, "[", "]") + "\n" +
                s3 + parseAndReplace(p3, NamPinyin::new, style, "[", "]");

    }
}
