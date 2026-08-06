package com.shuowen.yuzong.dict.data.model.Account;

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
