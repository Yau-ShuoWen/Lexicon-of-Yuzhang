package com.shuowen.yuzong.study.loadingtext;

import com.shuowen.yuzong.study.loadingtext.data.LoadingTextEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LoadingTextMapper
{
    List<LoadingTextEntity> findAll();

    LoadingTextEntity findById(Integer id);

    void insert(LoadingTextEntity entity);

    void update(LoadingTextEntity entity);

    void delete(Integer id);
}
