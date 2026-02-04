package com.shuowen.yuzong.data.domain.Pinyin;

import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Tool.RichTextUtil;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;

/**
 * 对于语言学者详细研究
 */
public class PinyinPreviewer
{
    /**
     * 预先设计好的文本展示
     */
    public static String getPreview(PinyinStyle style, Dialect d)
    {
        var s = d.castStyle(style); // 安全的强制转换
        return switch (d)
        {
            case NAM -> RichTextUtil.format("""
                    《枫桥夜泊》[fung1][qieu2][ia5][pok6]
                    [nyuot6][lok7][u1][ti2][song1][mon3][tien1]
                    [gong1][fung1][yu4][fo3][dui4][ceu2][mien4]
                    [gu1][su1][ceen2][uai5][hon2][san1][sii5]
                    [ia5][bon4][zung1][sen1][tau5][kak6][cuon2]
                    
                    《山行》[san1][xin4]
                    [yuon2][song5][hon2][san1][sak7][jin4][xia4]
                    [pak6][yun4][sang1][cu5][iu3][nin4][ga1]
                    [tiang2][ca1][co5][ngai4][fung1][lin4][uon3]
                    [song1][iet6][fung4][yu4][oe5][nyuot6][fa1]
                    
                    《江雪》[gong1][xyuot6]
                    [qien1][san1][nieu3][feei1][jyuot6]
                    [uon5][jin4][nin4][zung1][miet6]
                    [gu1][zeu1][suo1][lit7][ung4]
                    [tuk7][dieu4][hon2][gong1][xyuot6]
                    """, s, d);
        };
    }

    /**
     * 输入的某一个拼音的展示
     */

}
