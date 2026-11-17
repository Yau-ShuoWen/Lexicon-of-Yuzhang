package com.shuowen.yuzong.dict.hanzi.mapper;

import com.shuowen.yuzong.dict.hanzi.model.MdrChar;
import com.shuowen.yuzong.dict.hanzi.model.MdrCharWithHanzi;
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
    List<MdrCharWithHanzi> getInfoByCharacters(@Param("characters") java.util.Set<String> characters);
    List<MdrChar> getInfoByIds(@Param("ids") java.util.Set<Integer> ids);

    List<Integer> getMappedMandarinIds(@Param ("ids") List<Integer> ids,
                                       @Param ("excludeHanziId") Integer excludeHanziId,
                                       @Param ("dialect") String dialect);
}
