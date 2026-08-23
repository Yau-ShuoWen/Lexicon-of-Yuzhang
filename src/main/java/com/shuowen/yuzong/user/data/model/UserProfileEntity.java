package com.shuowen.yuzong.user.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserProfileEntity
{
    private Integer id;
    private String username;
    private String phone;
    private String authority;
    private List<String> permissions;
    private boolean admin;
}
