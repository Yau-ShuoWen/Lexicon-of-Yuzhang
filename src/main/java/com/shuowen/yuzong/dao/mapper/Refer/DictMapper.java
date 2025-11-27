package com.shuowen.yuzong.dao.mapper.Refer;

import com.shuowen.yuzong.dao.model.Refer.DictEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.*;

@Mapper
public interface DictMapper
{
    List<DictEntity> getAllDict();

    List<DictEntity> findDictByDialect(String lang);
}
