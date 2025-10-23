package com.shuowen.yuzong.dao.mapper.Character;

import com.shuowen.yuzong.dao.model.Character.MdrDialectChar;
import org.apache.ibatis.annotations.*;

import java.util.*;

@Mapper
public interface NamNotationMapper
{
    List<MdrDialectChar> selectMdrDialectChars(List<String> list);
}
