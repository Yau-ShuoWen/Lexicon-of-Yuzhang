package com.shuowen.yuzong.user.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserEntity
{
    private Integer id;
    private String username;
    private String password;
    private String authority;
}
