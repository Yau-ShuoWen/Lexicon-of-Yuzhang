package com.shuowen.yuzong.data.mapper.Character;

import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.data.model.Character.MdrChar;
import com.shuowen.yuzong.data.model.Character.DialectChar;
import org.apache.ibatis.annotations.*;

import java.util.*;

@Mapper
public interface PronunMapper
{
    /**
     * 获得所有GBK字符的简单信息
     */
    List<MdrChar> getAllMandarinInfo();

    /**
     * 根据简体字和繁体字获得普通话读音信息供选择
     */
    List<MdrChar> getMandarinInfoByScTc(String hanzi, String hantz, String dialect);

    /**
     * 用于标注读音使用
     */
    List<DialectChar> selectMandarinByChars(List<String> list, String dialect);


    /**
     * 编辑内容的时候获得
     */
    List<MdrChar> getMandarinInfoByDialectId(Integer n, String dialect);

    /**
     * @implNote 一个普通话「汉字-读音」组最多对应一个内容，所以当清空了却还有说明冲突。
     */
    List<MdrChar> getMandarinInfoByMandarinId(List<Integer> m, String dialect);


    /**
     * 根据方言汉字主键清空对应内容
     *
     * @param n 方言汉字主键
     * @implNote 因为信息量不大，所以直接删除再覆盖也不会有明显的性能问题
     */
    Integer clearMapByDialectId(Integer n, String dialect);


    /**
     * 插入新的信息
     */
    void insertMap(@Param ("data") List<Pair<Integer, Integer>> p, @Param ("dialect") String dialect);

}
