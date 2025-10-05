package com.shuowen.yuzong.service;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.dao.domain.Character.Hanzi;
import com.shuowen.yuzong.dao.domain.Character.HanziEntry;

import java.util.*;

public interface HanziService<T extends PinyinStyle, P extends Hanzi>
{
    P getHanziById(Integer id, T style);

    HanziEntry<P> getHanziScTc(String hanzi, T style);

    List<HanziEntry<P>> getHanziScTcGroup(String hanzi, String lang, T style);

    HanziEntry<P> getHanziVague(String hanzi, T style);

    List<HanziEntry<P>> getHanziVagueGroup(String hanzi, String lang, NamStyle style);
}