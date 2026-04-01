package com.shuowen.yuzong.data.mapper.IPA;

import com.shuowen.yuzong.data.model.IPA.IPAItem;
import com.shuowen.yuzong.data.model.IPA.IPASyllEntity;
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
    IPASyllEntity findSyllableByStandard(String standard, String dialect);

    /**
     * 按照编号为关键字，查询单行信息
     */
    IPASyllEntity findSyllableByCode(String code, String dialect);

    /**
     * 获取音节表的所有信息，按照编号排序
     */
    List<IPASyllEntity> getAllSyllable(String dialect);

    /**
     * 按照标准拼音为关键字，查询多行信息
     */
    Set<IPASyllEntity> findSyllableListByStandard(Set<String> list, String dialect);

    /**
     * 插入新的音节
     */
    void insertSyllable(IPASyllEntity pinyin, String dialect);

    /**
     * 修改音节的具体音标信息
     */
    void changeSyllableInfo(@Param ("py") IPASyllEntity pinyin, String dialect);

    // 音段表 -------------------------------

    /**
     * 按照编号为关键字，查询音段表单行信息
     */
    IPASyllEntity findSegmentInfo(String code, String dialect);

    /**
     * 按照编号为关键字，查询音段表多行信息
     */
    List<IPASyllEntity> findSegmentListByCode(List<String> list, String dialect);

    /**
     * 获取音段表的所有信息
     */
    List<IPASyllEntity> getAllSegment(String dialect);

    // 声调表 -------------------------------

    /**
     * 按照声调为关键字，查询声调表单行信息
     */
    IPAToneEntity findToneInfo(int tone, String dialect);

    /**
     * 按照声调为关键字，查询声调表多行信息
     */
    Set<IPAToneEntity> findToneInfoSet(Set<Integer> list, String dialect);

    /**
     * 获取声调表的所有信息
     */
    List<IPAToneEntity> getAllToneInfo(String dialect);

    /**
     *
     */
    List<IPAItem> getTableItem(String dialect, String key);
}
