package com.shuowen.yuzong.data.mapper.Account;

import com.shuowen.yuzong.data.model.Account.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper
{
    UserEntity getUserByName(String username);

    void insertUser(UserEntity user);

    void updateUsername(UserEntity user);

    void updatePassword(UserEntity user);
}
