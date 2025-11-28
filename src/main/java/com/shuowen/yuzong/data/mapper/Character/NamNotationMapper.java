package com.shuowen.yuzong.data.mapper.Character;

import com.shuowen.yuzong.data.model.Character.CharMdr;
import com.shuowen.yuzong.data.model.Character.DialectChar;
import org.apache.ibatis.annotations.*;

import java.util.*;

@Mapper
public interface NamNotationMapper
{
    /**
     * 用于标注读音使用
     */
    List<DialectChar> selectMdrDialectChars(List<String> list);

    /**
     * 编辑内容的时候获得
     */
    List<CharMdr> getMdrInfoByDialectId(Integer id);

    Integer clearMapByDialectId(Integer id);

    void insertMap(Integer m, Integer n);
}
