package com.shuowen.yuzong.user.data.mapper;

import com.shuowen.yuzong.user.data.model.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper
{
    UserEntity getUserByName(String username);

    void insertUser(UserEntity user);

    void updateUsername(UserEntity user);

    void updatePassword(UserEntity user);
}
