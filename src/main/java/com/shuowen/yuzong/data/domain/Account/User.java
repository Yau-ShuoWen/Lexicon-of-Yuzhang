package com.shuowen.yuzong.data.domain.Account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.format.JsonTool;
import com.shuowen.yuzong.data.model.Account.UserEntity;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;
import java.util.function.Function;

@Data
public class User
{
    private Integer id;
    private String username;
    private String password;
    private List<String> authority = null;

    @JsonIgnore
    private static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public User(UserEntity u)
    {
        id = u.getId();
        username = u.getUsername();
        password = u.getPassword();
        authority = JsonTool.readJson(u.getAuthority(), new TypeReference<>() {}, new ObjectMapper());
    }

    public User()
    {
    }

    public UserEntity transfer()
    {
        UserEntity u = new UserEntity();
        u.setId(id);
        u.setUsername(username);
        u.setPassword(password);
        u.setAuthority(JsonTool.toJson(authority, new ObjectMapper(), "[]"));
        return u;
    }

    public static User of(UserEntity u)
    {
        return new User(u);
    }

    public boolean isPasswordEqual(String password)
    {
        return encoder.matches(password, getPassword());
    }

    /**
     * 参数为未加密的密码
     */
    public void setPassword(String password)
    {
        this.password = encoder.encode(password);
    }

    /**
     * 尝试使用令牌构造
     */
    public static Maybe<User> tryOf(String t, Function<String, String> getName, Function<String, UserEntity> fun)
    {
        try
        {
            var username = Maybe.uncertain(getName.apply(t))
                    .getValueDirectly("登陆状态无效。Invalid login status.");
            return tryOf(username, fun);
        } catch (NullPointerException e)
        {
            return Maybe.nothing();
        }
    }

    /**
     * 尝试使用用户名构造
     */
    public static Maybe<User> tryOf(String username, Function<String, UserEntity> fun)
    {
        try
        {
            var user = Maybe.uncertain(fun.apply(username))
                    .getValueDirectly("用户不存在。User is not found.");
            return Maybe.exist(new User(user));
        } catch (NullPointerException e)
        {
            return Maybe.nothing();
        }
    }
}
