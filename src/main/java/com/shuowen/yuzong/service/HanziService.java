package com.shuowen.yuzong.service;

import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.data.domain.Character.HanziEdit;
import com.shuowen.yuzong.data.domain.IPA.Phonogram;
import com.shuowen.yuzong.data.domain.Character.HanziEntry;
import com.shuowen.yuzong.data.domain.IPA.IPASyllableStyle;
import com.shuowen.yuzong.data.domain.IPA.IPAToneStyle;
import com.shuowen.yuzong.data.dto.Character.HanziOutline;
import com.shuowen.yuzong.data.dto.Character.HanziShow;

import java.util.*;

public interface HanziService<T extends PinyinStyle>
{
    HanziEntry getHanziScTc(String hanzi);

    HanziEntry getHanziVague(String hanzi);

    List<HanziEntry> getHanziGroup(String hanzi, String lang, boolean vague);

    List<HanziShow> getHanziOrganize(String hanzi, String lang, boolean vague);

    List<HanziShow> getHanziFormatted(String hanzi, String lang, boolean vague, T style, Phonogram phonogram, IPAToneStyle ts, IPASyllableStyle ss);

    List<HanziOutline> filter(String hanzi);

    HanziEdit getHanziById(Integer id);

    void editHanzi(HanziEdit he);
}