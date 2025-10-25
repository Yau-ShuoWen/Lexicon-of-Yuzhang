package com.shuowen.yuzong.dao.mapper.Account;

import com.shuowen.yuzong.dao.model.Account.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper
{
    UserEntity getUser(String username);

    void insertUser(UserEntity user);
}
