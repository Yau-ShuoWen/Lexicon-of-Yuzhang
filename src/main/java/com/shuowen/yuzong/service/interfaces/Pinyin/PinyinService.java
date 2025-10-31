package com.shuowen.yuzong.service.interfaces.Pinyin;

import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.dao.domain.IPA.IPASyllableStyle;
import com.shuowen.yuzong.dao.domain.IPA.IPAToneStyle;


import java.util.*;

public interface PinyinService<T extends UniPinyin, S extends PinyinStyle>
{
    String getDefaultDict();

    Map<String, String> getAllIPASyllable(T p);

    Map<String, String> getAllIPATone(T p);

    Set<String> getDictionarySet();

    Map<String, String> getAllIPA(T p, IPAToneStyle ts,IPASyllableStyle ss);

    Map<T, Map<String, String>> getMultiLine(Set<T> p, IPAToneStyle ts, IPASyllableStyle ss);

    void insertSyllable(T p);

    Pair<Map<String, Integer>, Set<String>> check();

    void updateIPA();

    String getPreview(S style);
}
