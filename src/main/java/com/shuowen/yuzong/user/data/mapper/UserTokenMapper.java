package com.shuowen.yuzong.user.data.mapper;

import com.shuowen.yuzong.user.data.model.UserTokenEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserTokenMapper
{
    /** 插入一条 Token 记录 */
    void insert(UserTokenEntity token);

    /** 查询有效（未过期）Token 对应的用户名；无效返回 null */
    String selectUsername(@Param ("token") String token);

    /** 删除指定 Token */
    void deleteByToken(@Param ("token") String token);

    /** 删除指定用户的全部 Token */
    void deleteByUsername(@Param ("username") String username);

    /** 将有效 Token 的过期时间续期为当前时间 + seconds 秒 */
    void refresh(@Param ("token") String token, @Param ("seconds") long seconds);
}
