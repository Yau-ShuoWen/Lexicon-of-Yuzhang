package com.shuowen.yuzong.user.data.mapper;

import com.shuowen.yuzong.user.data.model.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper
{
    UserEntity getUserByName(@Param("username") String username);

    UserEntity getUserByPhone(@Param("phone") String phone);

    void insertUser(UserEntity user);

    void updateUsername(UserEntity user);

    void updatePassword(UserEntity user);

    void updatePhone(UserEntity user);
}
