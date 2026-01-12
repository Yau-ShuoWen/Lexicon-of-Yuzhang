package com.shuowen.yuzong.data.mapper.Character;

import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.data.model.Character.MdrChar;
import org.apache.ibatis.annotations.*;

import java.util.*;

@Mapper
public interface PronunMapper
{
    /**
     * 获得所有GBK字符的简单信息
     */
    List<String> getAllHanziInGBK();

    /**
     * 使用拼音筛选常用汉字
     */
    List<Integer> getHanziCommonByPinyin(String pinyin, String language);

    /**
     * 根据简体字和繁体字获得普通话读音信息供选择
     */
    List<MdrChar> getInfoByScTc(String sc, String tc, String dialect);

    /**
     * 编辑内容的时候获得
     */
    List<MdrChar> getInfoByDialectId(Integer dialectId, String dialect);

    /**
     * 一个普通话「汉字-读音」组最多对应一个内容，所以当清空了却还有说明冲突。
     */
    List<MdrChar> getInfoByMandarinId(List<Integer> mandarinIds, boolean all, String dialect);

    /**
     * 根据方言汉字主键清空对应内容
     */
    Integer clearMapByDialectId(Integer dialectId, String dialect);

    /**
     * 插入新的信息
     */
    void insertMap(@Param ("data") List<Pair<Integer, Integer>> data, @Param ("dialect") String dialect);


//    /**
//     * 用于标注读音使用
//     */
//    List<DialectChar> selectMandarinByChars(List<String> list, String dialect);
}
