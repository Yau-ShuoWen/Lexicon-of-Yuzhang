package com.shuowen.yuzong.service;

import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;
import com.shuowen.yuzong.dao.domain.IPA.IPAToneStyle;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public interface PinyinService<T extends UniPinyin, S extends PinyinStyle>
{
    Map<String, String> getAllIPASyllable(T p);

    Map<String, String> getAllIPATone(T p);

    Set<String> getDictionarySet();

    Map<String, String> getAllIPA(T p, IPAToneStyle ms);

    Map<T, Map<String, String>> getMultiLine(Set<T> p, IPAToneStyle ms);

    void insertSyllable(T p);

    Pair<Map<String, Integer>, Set<String>> check();

    void updateIPA();

    String getPreview(S style);
}
