package com.shuowen.yuzong.data.mapper.IPA;

import com.shuowen.yuzong.data.model.IPA.IPASyllableEntity;
import com.shuowen.yuzong.data.model.IPA.IPAToneEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface IPAMapper
{
    // 音节表-------------------------------

    /**
     * 按照标准拼音为关键字，查询单行信息
     */
    IPASyllableEntity findSyllableByStandard(String standard, String dialect);

    /**
     * 按照编号为关键字，查询单行信息
     */
    IPASyllableEntity findSyllableByCode(String code, String dialect);

    /**
     * 获取音节表的所有信息，按照编号排序
     */
    List<IPASyllableEntity> getAllSyllable(String dialect);

    /**
     * 按照标准拼音为关键字，查询多行信息
     */
    Set<IPASyllableEntity> findSyllableListByStandard(Set<String> list, String dialect);

    /**
     * 插入新的音节
     */
    void insertSyllable(IPASyllableEntity pinyin, String dialect);

    /**
     * 修改音节的具体音标信息
     * */
    void changeSyllableInfo(@Param ("py") IPASyllableEntity pinyin, String dialect);

    // 音段表 -------------------------------

    /**
     * 按照编号为关键字，查询音段表单行信息
     */
    IPASyllableEntity findSegmentByCode(String code, String dialect);

    /**
     * 按照编号为关键字，查询音段表多行信息
     */
    List<IPASyllableEntity> findSegmentListByCode(List<String> list, String dialect);

    /**
     * 获取音段表的所有信息
     */
    List<IPASyllableEntity> getAllSegment(String dialect);

    // 声调表 -------------------------------

    /**
     * 按照声调为关键字，查询声调表单行信息
     */
    IPAToneEntity findToneByTone(int tone, String dialect);

    /**
     * 按照声调为关键字，查询声调表多行信息
     */
    Set<IPAToneEntity> findToneListByTone(Set<Integer> list, String dialect);

    /**
     * 获取声调表的所有信息
     */
    List<IPAToneEntity> getAllTone(String dialect);
}
