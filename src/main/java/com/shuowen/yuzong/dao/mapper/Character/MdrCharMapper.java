package com.shuowen.yuzong.dao.mapper.Character;

import com.shuowen.yuzong.dao.model.Character.CharMdr;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MdrCharMapper
{
    List<CharMdr> getInfo(String hanzi, String hantz);
}
