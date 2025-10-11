package com.shuowen.yuzong.dao.mapper.PinyinIPA;

import com.shuowen.yuzong.dao.model.IPA.IPASyllableEntity;

import com.shuowen.yuzong.dao.model.IPA.IPAToneEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.*;

@Mapper
public interface NamIPAMapper
{
    // 音节表-------------------------------

    /**
     * 按照拼音为关键字，查询单行信息
     */
    IPASyllableEntity findByPinyin(String pinyin);

    /**
     * 按照编号为关键字，查询单行信息
     */
    IPASyllableEntity findByCode(String code);

    /**
     * 获取音节表的所有信息，按照编号排序
     */
    List<IPASyllableEntity> findAllPinyin();


    Set<IPASyllableEntity> findAllPinyinList(Set<String> list);

    void insertPinyin(IPASyllableEntity pinyin);

    void changeInfo(IPASyllableEntity pinyin);

    // 音节成分表-------------------------------

    /**
     * 通过带空位的code查询
     *
     * @param code 声母或者韵母的号码<ul>
     *             <li>声母的格式为xx~~~</li>
     *             <li>韵母的格式为~~xxx</li>
     *             </ul>
     */
    IPASyllableEntity findElement(String code);

    List<IPASyllableEntity> findAllElement();

    List<IPASyllableEntity> findAllElementList(List<String> list);

    // 声调表 -------------------------------

    /**
     * 按照声调为关键字，查询单行信息
     */
    IPAToneEntity findByTone(int tone);

    /**
     * 获取声调表的所有信息
     */
    List<IPAToneEntity> findAllTone();

    Set<IPAToneEntity> findAllToneList(Set<String> list);
}
