package com.shuowen.yuzong.service;

import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Tool.dataStructure.Status;
import com.shuowen.yuzong.dao.domain.Character.Hanzi;
import com.shuowen.yuzong.dao.domain.Character.HanziEntry;
import com.shuowen.yuzong.dao.dto.HanziShow;

import java.util.*;

public interface HanziService<T extends PinyinStyle, P extends Hanzi>
{
    P getHanziById(Integer id, T style, Status statue);


    HanziEntry<P> getHanziScTc(String hanzi, T style, Status status);

    List<HanziEntry<P>> getHanziScTcGroup(String hanzi, T style, Status statue, String lang);

    List<HanziShow> getHanziScTcOrganize(String hanzi, T style, Status statue, String lang);


    HanziEntry<P> getHanziVague(String hanzi, T style, Status statue);

    List<HanziEntry<P>> getHanziVagueGroup(String hanzi, T style, Status statue, String lang);

    List<HanziShow> getHanziVagueOrganize(String hanzi, T style, Status statue, String lang);
}