package com.shuowen.yuzong.user.controller;

import com.shuowen.yuzong.user.data.mapper.UserMapper;
import com.shuowen.yuzong.user.data.mapper.UserSettingMapper;
import com.shuowen.yuzong.user.data.model.UserEntity;
import com.shuowen.yuzong.user.service.TokenService;
import com.shuowen.yuzong.user.service.UserService;
import com.shuowen.yuzong.util.err.APIException;
import com.shuowen.yuzong.util.tuple.APIResponse;
import com.shuowen.yuzong.util.tuple.HTTPCode;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController
{
    private final UserService users;
    private final UserMapper userMapper;
    private final UserSettingMapper settings;
    private final TokenService tokens;

    public record UserDetails(Integer id, String username, String phone, String authority,
                              boolean admin, String lacKeyboardScheme) {}
    public record AccountChange(String username, String phone) {}
    public record SettingChange(String lacKeyboardScheme) {}
    public record UserSummary(Integer id, String username, String phone, boolean admin) {}
    public record UserPage(List<UserSummary> users, Integer nextAfterId) {}

    @GetMapping("/list")
    public APIResponse<UserPage> list(@RequestParam(defaultValue = "0") Integer afterId)
    {
        if (afterId == null || afterId < 0) throw new IllegalArgumentException("用户编号无效");
        List<UserEntity> rows = userMapper.listUsersAfter(afterId, 21);
        List<UserSummary> summaries = rows.stream().limit(20)
                .map(user -> new UserSummary(user.getId(), user.getUsername(), user.getPhone(),
                        users.hasAdminAuthority(user.getAuthority())))
                .toList();
        Integer next = rows.size() > 20 ? summaries.get(summaries.size() - 1).id() : null;
        return APIResponse.success(new UserPage(summaries, next));
    }

    @GetMapping
    public APIResponse<UserDetails> search(@RequestParam String keyword)
    {
        String value = keyword == null ? "" : keyword.trim();
        if (value.isEmpty()) throw new IllegalArgumentException("请输入用户名、手机号或用户编号");
        UserEntity match;
        if (value.matches("\\d+"))
        {
            match = null;
            try { match = users.getUserById(Integer.valueOf(value)); }
            catch (NumberFormatException ignored) { }
            if (match == null) match = userMapper.getUserByPhone(value);
        }
        else
        {
            match = userMapper.getUserByName(value);
            if (match == null) match = userMapper.getUserByPhone(value);
        }
        return APIResponse.success(match == null ? null : details(match));
    }

    @GetMapping("/{userId}")
    public APIResponse<UserDetails> get(@PathVariable Integer userId)
    {
        return APIResponse.success(details(requireUser(userId)));
    }

    @PatchMapping("/{userId}/account")
    @Transactional
    public APIResponse<UserDetails> updateAccount(@PathVariable Integer userId,
                                                   @RequestBody AccountChange change)
    {
        UserEntity user = requireUser(userId);
        if (change == null) throw new IllegalArgumentException("账号内容不能为空");
        String username = change.username() == null ? "" : change.username().trim();
        String phone = change.phone() == null ? "" : change.phone().trim();
        if (username.isEmpty() || username.matches("\\d+"))
            throw new IllegalArgumentException("用户名不能为空或纯数字");
        if (!phone.isEmpty() && !phone.matches("1\\d{10}"))
            throw new IllegalArgumentException("手机号格式无效");
        UserEntity sameName = userMapper.getUserByName(username);
        if (sameName != null && !sameName.getId().equals(userId))
            throw new IllegalArgumentException("用户名重复");
        UserEntity samePhone = phone.isEmpty() ? null : userMapper.getUserByPhone(phone);
        if (samePhone != null && !samePhone.getId().equals(userId))
            throw new IllegalArgumentException("手机号重复");
        if (!username.equals(user.getUsername()))
        {
            String previous = user.getUsername();
            user.setUsername(username);
            userMapper.updateUsername(user);
            tokens.forceLogout(previous);
        }
        String normalizedPhone = phone.isEmpty() ? null : phone;
        if (!java.util.Objects.equals(normalizedPhone, user.getPhone()))
        {
            user.setPhone(normalizedPhone);
            userMapper.updatePhone(user);
        }
        return APIResponse.success(details(user));
    }

    @PatchMapping("/{userId}/settings")
    @Transactional
    public APIResponse<UserDetails> updateSettings(@PathVariable Integer userId,
                                                    @RequestBody SettingChange change)
    {
        UserEntity user = requireUser(userId);
        if (change == null || !("gon".equals(change.lacKeyboardScheme()) || "yzc".equals(change.lacKeyboardScheme())))
            throw new IllegalArgumentException("键盘方案无效");
        settings.ensureVersion(userId);
        settings.lockVersion(userId);
        settings.upsertKeyboardScheme(userId, change.lacKeyboardScheme());
        settings.incrementVersion(userId);
        return APIResponse.success(details(user));
    }

    private UserEntity requireUser(Integer id)
    {
        UserEntity user = id == null ? null : users.getUserById(id);
        if (user == null) throw new APIException(HTTPCode.NotFound, "用户不存在");
        return user;
    }

    private UserDetails details(UserEntity user)
    {
        String scheme = settings.getKeyboardScheme(user.getId());
        return new UserDetails(user.getId(), user.getUsername(), user.getPhone(), user.getAuthority(),
                users.hasAdminAuthority(user.getAuthority()), "yzc".equals(scheme) ? "yzc" : "gon");
    }
}
