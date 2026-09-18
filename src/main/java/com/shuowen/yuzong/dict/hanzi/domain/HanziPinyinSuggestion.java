package com.shuowen.yuzong.dict.hanzi.domain;

import com.shuowen.yuzong.linguistics.util.RPinyin;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/** 汉字编辑页中只读展示的、由词语反推出来的未收录读音。 */
@Data
public class HanziPinyinSuggestion
{
    private final RPinyin pinyin;
    private final List<HanziShow.Word> words = new ArrayList<>();
}
