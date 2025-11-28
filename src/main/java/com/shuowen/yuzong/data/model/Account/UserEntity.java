package com.shuowen.yuzong.data.model.Account;

import lombok.Data;

@Data
public class UserEntity
{
    private Integer id;
    private String username;
    private String password;
    private String authority;
}
