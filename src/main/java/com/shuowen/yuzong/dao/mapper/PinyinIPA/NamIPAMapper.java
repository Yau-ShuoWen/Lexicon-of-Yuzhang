package com.shuowen.yuzong.dao.mapper.PinyinIPA;

import com.shuowen.yuzong.dao.model.PinyinIPA.NamIPA;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NamIPAMapper
{
    /**
     * 按照拼音为关键字查询单行信息
     * */
    @Select ("select * from NC.nam_ipa where standard = #{pinyin}")
    NamIPA findByPinyin(@Param ("pinyin") String pinyin);

    /**
     * 按照代码为关键字查询单行信息
     * */
    @Select ("select * from NC.nam_ipa where code = #{code}")
    NamIPA findByCode(@Param ("code") String code);

    /**
     * 获取一张表的所有信息，按照代码排序
     * */
    @Select ("select * from NC.nam_ipa order by code")
    List<NamIPA> findAll();

    /**
     * 通过带空位的code查询
     *
     * @param code 声母或者韵母的号码<ul>
     *             <li>声母的格式为xx~~~</li>
     *             <li>韵母的格式为~~xxx</li>
     *             </ul>
     */
    @Select ("select * from NC.nam_ipa_res where code = #{code}")
    NamIPA consultByCode(@Param ("code") String code);
}
