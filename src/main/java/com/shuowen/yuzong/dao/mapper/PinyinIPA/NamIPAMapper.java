package com.shuowen.yuzong.dao.mapper.PinyinIPA;

import com.shuowen.yuzong.dao.model.IPA.IPASyllableEntity;

import com.shuowen.yuzong.dao.model.IPA.IPAToneEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.*;

@Mapper
public interface NamIPAMapper
{
    // 音节表-------------------------------

    /**
     * 按照拼音为关键字，查询单行信息
     * */
    @Select ("select * from NC.nam_ipa_syllable where standard = #{pinyin}")
    IPASyllableEntity findByPinyin(String pinyin);

    /**
     * 按照编号为关键字，查询单行信息
     * */
    @Select ("select * from NC.nam_ipa_syllable where code = #{code}")
    IPASyllableEntity findByCode(String code);

    /**
     * 获取音节表的所有信息，按照编号排序
     * */
    @Select ("select * from NC.nam_ipa_syllable order by code")
    List<IPASyllableEntity> findAllPinyin();


    @Select ("<script>" +
            "SELECT * FROM NC.nam_ipa_syllable WHERE standard IN " +
            "<foreach collection='list' item='item' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach>" +
            "</script>")
    Set<IPASyllableEntity> findAllPinyinList(Set<String> list);



    // 音节成分表-------------------------------

    /**
     * 通过带空位的code查询
     *
     * @param code 声母或者韵母的号码<ul>
     *             <li>声母的格式为xx~~~</li>
     *             <li>韵母的格式为~~xxx</li>
     *             </ul>
     */
    @Select ("select * from NC.nam_ipa_element where code = #{code}")
    IPASyllableEntity consultByCode(String code);




    // 声调表 -------------------------------

    /**
     * 按照声调为关键字，查询单行信息
     * */
    @Select ("select * from NC.nam_ipa_tone where standard = #{tone}")
    IPAToneEntity findByTone(int tone);

    /**
     * 获取声调表的所有信息
     * */
    @Select ("select * from NC.nam_ipa_tone order by standard")
    List<IPAToneEntity> findAllTone();

    @Select ("<script>" +
            "SELECT * FROM NC.nam_ipa_tone WHERE standard IN " +
            "<foreach collection='list' item='item' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach>" +
            "</script>")
    Set<IPAToneEntity> findAllToneList(Set<String> list);
}
