package com.shuowen.yuzong.data.mapper.Refer;

import com.shuowen.yuzong.data.model.Refer.DictEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.*;

@Mapper
public interface DictMapper
{
    List<DictEntity> getAllDict();

    List<DictEntity> findDictByDialect(String lang);
}
