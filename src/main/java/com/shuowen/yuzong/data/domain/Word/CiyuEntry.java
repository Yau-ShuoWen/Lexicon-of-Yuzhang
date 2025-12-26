package com.shuowen.yuzong.data.domain.Word;

import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.data.model.Word.WordEntity;
import lombok.Data;

import java.util.*;

/**
 * 做一个包装类的原因是好统一管理简繁体。
 */

@Data
public class CiyuEntry
{
    private final List<Ciyu> list;
    private final Language language;

    public CiyuEntry(List<WordEntity> wd, Language language)
    {
        this.language = language;
        list = Ciyu.listOf(wd, language);
    }

    public static CiyuEntry of(List<WordEntity> wd, Language language)
    {
        return new CiyuEntry(wd, language);
    }
}
