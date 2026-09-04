package com.shuowen.yuzong.study.streak;

import com.shuowen.yuzong.study.streak.data.StreakOverview;
import com.shuowen.yuzong.study.streak.data.StreakRecordEntity;
import com.shuowen.yuzong.study.streak.data.StreakRecordUpdateRequest;
import com.shuowen.yuzong.study.streak.data.StreakUserSummary;
import com.shuowen.yuzong.user.data.model.UserEntity;
import com.shuowen.yuzong.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class StreakAdminService
{
    private static final List<String> VALID_STATUSES = List.of("completed", "protected", "missed");

    private final StreakMapper streakMapper;
    private final UserService userService;
    private final StreakShowService streakShowService;

    public StreakAdminService(StreakMapper streakMapper,
                              UserService userService,
                              StreakShowService streakShowService)
    {
        this.streakMapper = streakMapper;
        this.userService = userService;
        this.streakShowService = streakShowService;
    }

    public List<StreakUserSummary> searchUsers(String token, String keyword)
    {
        assertAdmin(token);
        String normalized = keyword == null ? "" : keyword.trim();
        if (normalized.isEmpty()) throw new IllegalArgumentException("请输入用户名、手机号或用户编号");

        UserEntity exact = findById(normalized);
        List<UserEntity> users = exact == null
                ? userService.searchUsers(normalized)
                : List.of(exact);
        if (users == null) users = List.of();
        return users.stream()
                .filter(user -> user != null)
                .map(user -> new StreakUserSummary(user.getId(), user.getUsername(), user.getPhone()))
                .toList();
    }

    public StreakOverview getOverview(String token, Integer userId, LocalDate from, LocalDate to)
    {
        assertAdmin(token);
        requireUser(userId);
        return streakShowService.getOverviewForUser(userId, from, to);
    }

    public void updateRecord(String token, Integer userId, StreakRecordUpdateRequest request)
    {
        assertAdmin(token);
        requireUser(userId);
        if (request == null || request.getDate() == null)
        {
            throw new IllegalArgumentException("日期不能为空");
        }
        if (request.getDate().isAfter(LocalDate.now()))
        {
            throw new IllegalArgumentException("不能修改未来日期的连胜记录");
        }
        if (!VALID_STATUSES.contains(request.getStatus()))
        {
            throw new IllegalArgumentException("连胜标记无效");
        }

        StreakRecordEntity existing = streakMapper.findByUserAndDate(userId, request.getDate());
        if (existing == null)
        {
            streakMapper.insert(new StreakRecordEntity(null, userId, request.getDate(), request.getStatus()));
        }
        else if (!request.getStatus().equals(existing.getStatus()))
        {
            streakMapper.updateStatus(userId, request.getDate(), request.getStatus());
        }
    }

    private UserEntity findById(String value)
    {
        try
        {
            return userService.getUserById(Integer.valueOf(value));
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    private UserEntity requireUser(Integer userId)
    {
        if (userId == null) throw new IllegalArgumentException("用户编号不能为空");
        UserEntity user = userService.getUserById(userId);
        if (user == null) throw new IllegalArgumentException("用户不存在");
        return user;
    }

    private void assertAdmin(String token)
    {
        UserEntity operator = userService.getUserByToken(token);
        if (!userService.hasAdminAuthority(operator.getAuthority()))
        {
            throw new IllegalArgumentException("管理员权限不足");
        }
    }
}
