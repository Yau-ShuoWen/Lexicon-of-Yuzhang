package com.shuowen.yuzong.data.mapper.IPA;

import com.shuowen.yuzong.data.model.IPA.IPASyllableEntity;
import com.shuowen.yuzong.data.model.IPA.IPAToneEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

@Mapper
public interface IPAMapper
{
    // 音节表-------------------------------

    /**
     * 按照拼音为关键字，查询单行信息
     */
    IPASyllableEntity findByPinyin(String pinyin, String dialect);

    /**
     * 按照编号为关键字，查询单行信息
     */
    IPASyllableEntity findByCode(String code, String dialect);

    /**
     * 获取音节表的所有信息，按照编号排序
     */
    List<IPASyllableEntity> findAllPinyin(String dialect);


    Set<IPASyllableEntity> findAllPinyinList(Set<String> list, String dialect);

    void insertPinyin(IPASyllableEntity pinyin, String dialect);

    void changeInfo(IPASyllableEntity pinyin, String dialect);

    // 音节成分表-------------------------------

    /**
     * 通过带空位的code查询
     *
     * @param code 声母或者韵母的号码<ul>
     *             <li>声母的格式为xx~~~</li>
     *             <li>韵母的格式为~~xxx</li>
     *             </ul>
     */
    IPASyllableEntity findElement(String code, String dialect);

    List<IPASyllableEntity> findAllElement(String dialect);

    List<IPASyllableEntity> findAllElementList(List<String> list, String dialect);

    // 声调表 -------------------------------

    /**
     * 按照声调为关键字，查询单行信息
     */
    IPAToneEntity findByTone(int tone, String dialect);

    /**
     * 获取声调表的所有信息
     */
    List<IPAToneEntity> findAllTone(String dialect);

    Set<IPAToneEntity> findAllToneList(Set<String> list, String dialect);
}
