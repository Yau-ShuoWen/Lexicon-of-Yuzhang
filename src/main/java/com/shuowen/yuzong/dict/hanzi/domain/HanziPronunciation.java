package com.shuowen.yuzong.dict.hanzi.domain;

import com.shuowen.yuzong.linguistics.util.KeyboardPinyin;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.ext.other.ObjectTool;
import com.shuowen.yuzong.util.text.ScTcText;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/** 一个汉字的一个有序读音项。 */
@Data
@NoArgsConstructor
public class HanziPronunciation
{
    private KeyboardPinyin pinyin;
    private String code;
    private ScTcText tag;
    private List<Integer> mandarin = new ArrayList<>();

    public HanziPronunciation(KeyboardPinyin pinyin, String code, ScTcText tag, List<Integer> mandarin)
    {
        this.pinyin = pinyin;
        this.code = code;
        this.tag = tag;
        this.mandarin = mandarin == null ? new ArrayList<>() : new ArrayList<>(mandarin);
    }

    /** 不信任客户端提交的数据库拼音和排序码，统一重新生成。 */
    public HanziPronunciation normalize(Dialect dialect)
    {
        ObjectTool.asserts(tag != null, "拼音标签不能为空");
        var normalized = dialect.checkAndCreatePinyin(pinyin);
        return new HanziPronunciation(
                KeyboardPinyin.of(normalized.toDatabasePinyin().toString(true)),
                normalized.getWeight(),
                tag,
                mandarin
        );
    }
}
