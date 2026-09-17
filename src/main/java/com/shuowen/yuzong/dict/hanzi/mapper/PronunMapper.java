package com.shuowen.yuzong.dict.hanzi.mapper;

import com.shuowen.yuzong.dict.hanzi.model.MdrChar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PronunMapper
{
    List<String> getAllHanziInGBK();

    List<String> getAllHanziFreq();

    List<String> findHanziFreqByPinyin(String syll, Integer tone);

    List<Integer> getHanziCommonByPinyin(String pinyin, String language);

    List<MdrChar> getInfoByScTc(String sc, String tc);

    List<Integer> getMappedMandarinIds(@Param ("ids") List<Integer> ids,
                                       @Param ("excludeHanziId") Integer excludeHanziId,
                                       @Param ("dialect") String dialect);
}
