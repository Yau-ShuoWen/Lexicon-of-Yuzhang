package com.shuowen.yuzong.data.domain.Account;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.data.model.Account.UserEntity;
import lombok.Data;

import java.util.*;

import static com.shuowen.yuzong.Tool.format.JsonTool.readJson;
import static com.shuowen.yuzong.Tool.format.JsonTool.toJson;

@Data
public class User
{
    private Integer id;
    private String username;
    private String password;
    private List<String> authority=null;

    public User(UserEntity u)
    {
        id = u.getId();
        username = u.getUsername();
        password = u.getPassword();
        authority = readJson(u.getAuthority(), new TypeReference<>() {}, new ObjectMapper());
    }

    public User(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    public UserEntity transfer()
    {
        UserEntity u = new UserEntity();
        u.setId(id);
        u.setUsername(username);
        u.setPassword(password);
        u.setAuthority(toJson(authority, new ObjectMapper(), "[]"));
        return u;
    }

    public static User of(UserEntity u)
    {
        return new User(u);
    }

    public static User of(String username, String password)
    {
        return new User(username, password);
    }

}
