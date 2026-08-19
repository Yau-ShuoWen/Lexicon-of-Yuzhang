package com.shuowen.yuzong.user.data.mapper;

import com.shuowen.yuzong.user.data.model.UserVerificationCodeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserVerificationCodeMapper
{
    void insert(UserVerificationCodeEntity code);

    UserVerificationCodeEntity selectLatestByPhone(@Param("phone") String phone, @Param("purpose") String purpose);

    UserVerificationCodeEntity selectValidByPhoneAndCode(@Param("phone") String phone,
                                                         @Param("code") String code,
                                                         @Param("purpose") String purpose);

    void markUsed(@Param("id") Long id);

    void deleteExpired();

    List<UserVerificationCodeEntity> listRecentByPhone(@Param("phone") String phone, @Param("limit") int limit);
}
