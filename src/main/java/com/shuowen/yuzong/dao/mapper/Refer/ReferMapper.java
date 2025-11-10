package com.shuowen.yuzong.dao.mapper.Refer;

import com.shuowen.yuzong.dao.model.Refer.ReferEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.*;

@Mapper
public interface ReferMapper
{
    /**
     * 模糊搜索
     */
    List<ReferEntity> fuzzySearch(String keyword, String dictionary);

    /**
     * 指定页码搜索
     * */
    List<ReferEntity> pageSearch(Integer page, String dictionary);

    /**
     * 搜索上下文
     */
    List<ReferEntity> getContext(Integer id, String dictionary);

    void insert(ReferEntity refer);

    void update(ReferEntity refer,List<String> list);

    void delete(Integer id);
}
