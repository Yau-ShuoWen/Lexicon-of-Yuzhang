package com.shuowen.yuzong.service;

import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Scheme.NamPinyin;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;

import java.util.*;

public interface PinyinService<T extends UniPinyin, S extends PinyinStyle>
{
    String getIPASyllable(T p, String dict);

    String getIPATone(T p, String dict);

    String getIPA(T p, String dict);

    Map<String, String> getAllIPASyllable(T p);

    Map<String, String> getAllIPATone(T p);

    Set<String> getDictonarySet();

    Map<String, String> getAllIPA(T p);

    Map<NamPinyin, Map<String, String>> getMultiLine(Set<T> p);

    String getPreview(S style);
}
