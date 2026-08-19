package com.shuowen.yuzong.user.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileEntity
{
    private Integer id;
    private String username;
    private String phone;
    private String authority;
    private boolean admin;
}
