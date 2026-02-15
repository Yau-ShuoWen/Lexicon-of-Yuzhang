package com.shuowen.yuzong.data.mapper.Reference;

import com.shuowen.yuzong.data.model.Reference.DictEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.*;

@Mapper
public interface DictMapper
{
    List<DictEntity> getAll();

    List<DictEntity> findByDialect(String dialect);
}
