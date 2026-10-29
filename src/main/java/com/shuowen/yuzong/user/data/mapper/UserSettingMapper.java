package com.shuowen.yuzong.user.data.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserSettingMapper
{
    String getLegacyPinyin(@Param("userId") Integer userId);

    void upsertLegacyPinyin(@Param("userId") Integer userId, @Param("value") String value);

    void ensureVersion(@Param("userId") Integer userId);

    Long getVersion(@Param("userId") Integer userId);

    Long lockVersion(@Param("userId") Integer userId);

    void incrementVersion(@Param("userId") Integer userId);
}
