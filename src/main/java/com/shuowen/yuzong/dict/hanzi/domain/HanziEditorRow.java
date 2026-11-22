package com.shuowen.yuzong.dict.hanzi.domain;

import com.shuowen.yuzong.util.text.ScTcText;
import com.shuowen.yuzong.dict.data.domain.setting.NoteTag;
import com.shuowen.yuzong.util.tuple.Pair;

import java.util.List;

/** 编辑汉字列表的一行，读音保持原有顺序。 */
public record HanziEditorRow(Integer id, String sc, String tc, List<Pronunciation> pinyin,
                             List<Pair<NoteTag, ScTcText>> note)
{
    public record Pronunciation(String pinyin, ScTcText tag, List<String> mandarin) {}
}
