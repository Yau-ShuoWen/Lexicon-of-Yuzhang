package com.shuowen.yuzong.service;

import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Tool.dataStructure.Status;
import com.shuowen.yuzong.dao.domain.Character.Hanzi;
import com.shuowen.yuzong.dao.domain.Character.HanziEntry;
import com.shuowen.yuzong.dao.domain.IPA.IPASyllableStyle;
import com.shuowen.yuzong.dao.domain.IPA.IPAToneStyle;
import com.shuowen.yuzong.dao.dto.HanziShow;

import java.util.*;

public interface HanziService<T extends PinyinStyle>
{
    Hanzi getHanziById(Integer id);

    HanziEntry getHanziScTc(String hanzi);

    HanziEntry getHanziVague(String hanzi);

    List<HanziEntry> getHanziGroup(String hanzi, String lang, boolean vague);

    List<HanziShow> getHanziOrganize(String hanzi, String lang, boolean vague);

    List<HanziShow> getHanziFormatted(String hanzi, String lang, boolean vague, T style, Status status, IPAToneStyle ts, IPASyllableStyle ss);
}