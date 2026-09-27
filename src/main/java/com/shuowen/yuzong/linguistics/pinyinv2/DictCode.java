package com.shuowen.yuzong.linguistics.pinyinv2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.text.ScTcText;
import lombok.Getter;

@Getter
public enum DictCode
{
    STANDARD("南昌话拼音标准音",
            "sc", Dialect.LAC, false, true, null),
    NCDICT("南昌方言詞典",
            "tc", Dialect.LAC, true, false, null),
    NCSTUDY("南昌方言研究",
            "sc", Dialect.LAC, true, false, null),
    NCPHON("南昌音系",
            "tc", Dialect.LAC, true, false, null),
    NCPHON_Y("南昌音系(严式标音)",
            "tc", Dialect.LAC, false, false, "NCPHON"),
    NCRECORD("南昌話音檔",
            "tc", Dialect.LAC, true, false, null),
    NCDIALSTD("漢語方言規範",
            "tc", Dialect.LAC, true, false, null),
    CNDIALDICT("漢語方音字彙第二版",
            "tc", Dialect.LAC, true, false, null),
    GANSUM("贛方言概要",
            "tc", Dialect.LAC, true, false, null),

    ;
    private final ScTcText bookname;
    private final Language language;
    private final Dialect dialect;
    /** 是否作为一部独立辞书参与辞书列表、编辑等书本流程。 */
    private final boolean completeDictionary;
    /** 是否为该方言没有指定辞书时使用的默认 IPA 方案。 */
    private final boolean fallback;
    /** 派生记音版本所属的中心辞书；中心辞书及兜底方案为空。 */
    private final String centralCode;

    DictCode(
            String name,
            String language,
            Dialect dialect,
            boolean completeDictionary,
            boolean fallback,
            String centralCode
    )
    {
        this.bookname = new ScTcText(name);
        this.language = Language.of(language);
        this.dialect = dialect;
        this.completeDictionary = completeDictionary;
        this.fallback = fallback;
        this.centralCode = centralCode;
    }

    /**
     * 文本、书名等书本级功能应使用中心辞书；普通辞书和兜底方案返回自身。
     */
    public DictCode centralDictionary()
    {
        return centralCode == null ? this : valueOf(centralCode);
    }

    public boolean derived()
    {
        return centralCode != null;
    }

    @JsonCreator
    public static DictCode of(String code)
    {
        for (var dict : values())
        {
            if (dict.name().equalsIgnoreCase(code)) return dict;
        }
        throw new IllegalArgumentException("辞书代号无效：" + code);
    }

    @JsonValue
    @Override
    public String toString()
    {
        return name().toLowerCase();
    }
}
