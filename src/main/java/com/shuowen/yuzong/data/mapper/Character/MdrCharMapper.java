package com.shuowen.yuzong.data.mapper.Character;

import com.shuowen.yuzong.data.model.Character.CharMdr;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MdrCharMapper
{
    List<CharMdr> getInfo(String hanzi, String hantz);
}
