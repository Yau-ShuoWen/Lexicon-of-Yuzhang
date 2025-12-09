package com.shuowen.yuzong.data.mapper.Character;

import com.shuowen.yuzong.data.model.Character.CharMdr;
import com.shuowen.yuzong.data.model.Character.DialectChar;
import org.apache.ibatis.annotations.*;

import java.util.*;

@Mapper
public interface PronunMapper
{
    List<CharMdr> getMandarinInfo(String hanzi, String hantz, Integer dialectId, String dialect);

    /**
     * 用于标注读音使用
     */
    List<DialectChar> selectMdrDialectChars(List<String> list, String dialect);

    /**
     * 编辑内容的时候获得
     */
    List<CharMdr> getMdrInfoByDialectId(Integer id, String dialect);

    /**
     * 因为没有重要信息，所以直接可以删除，根据方言代码删除
     */
    Integer clearMapByDialectId(Integer id, String dialect);

    /**
     * 插入新的信息
     */
    void insertMap(Integer m, Integer n, String dialect);
}
