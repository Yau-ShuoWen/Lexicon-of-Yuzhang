package com.shuowen.yuzong.user.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录令牌实体（对应表 NC.user_token，用于数据库版 Token 存储）。
 */
@Data
@AllArgsConstructor
public class UserTokenEntity
{
    /** 登录令牌 */
    private String token;

    /** 所属用户 */
    private String username;

    /** 过期时间 */
    private LocalDateTime expireAt;

    /** 创建时间（数据库默认当前时间，插入时可留空） */
    private LocalDateTime createAt;
}
