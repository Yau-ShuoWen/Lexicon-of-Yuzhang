package com.shuowen.yuzong.study.service;

import com.shuowen.yuzong.study.data.dto.StreakOverview;
import com.shuowen.yuzong.study.data.mapper.StreakMapper;
import com.shuowen.yuzong.study.data.model.StreakRecordEntity;
import com.shuowen.yuzong.user.data.model.UserEntity;
import com.shuowen.yuzong.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class StreakSimulationService
{
    private final StreakMapper mapper;
    private final UserService userService;
    private final StreakService streakService;

    public StreakSimulationService(StreakMapper mapper,
                                   UserService userService,
                                   StreakService streakService)
    {
        this.mapper = mapper;
        this.userService = userService;
        this.streakService = streakService;
    }

    public StreakOverview getOverview(String token, LocalDate from, LocalDate to, LocalDate simulatedToday)
    {
        UserEntity user = userService.getUserByToken(token);
        assertAdmin(user);
        settleExpiredDays(user.getId(), simulatedToday);
        List<StreakRecordEntity> records = mapper.findByUserAndRange(user.getId(), from, to);
        return streakService.buildOverview(records, mapper.findAllByUser(user.getId()), simulatedToday,
                protectionBalance(user.getId()));
    }

    public void mark(String token, LocalDate date, String status)
    {
        if (date == null || date.isAfter(LocalDate.now().plusYears(10)))
        {
            throw new IllegalArgumentException("模拟日期无效");
        }
        if (!List.of("completed", "protected", "missed").contains(status))
        {
            throw new IllegalArgumentException("连胜标记无效");
        }
        UserEntity user = userService.getUserByToken(token);
        assertAdmin(user);
        StreakRecordEntity existing = mapper.findByUserAndDate(user.getId(), date);
        if (existing == null)
        {
            mapper.insert(new StreakRecordEntity(null, user.getId(), date, status));
        }
        else
        {
            mapper.updateStatus(user.getId(), date, status);
        }
    }

    public void completeActivity(String token, LocalDate date)
    {
        UserEntity user = userService.getUserByToken(token);
        assertAdmin(user);
        settleExpiredDays(user.getId(), date);
        mark(token, date, "completed");
    }

    public void clear(String token)
    {
        UserEntity user = userService.getUserByToken(token);
        assertAdmin(user);
        mapper.deleteByUser(user.getId());
        Integer balance = mapper.findProtectionBalance(user.getId());
        if (balance != null) mapper.resetProtectionBalance(user.getId());
    }

    public int addProtection(String token, int amount)
    {
        if (amount <= 0 || amount > 100)
        {
            throw new IllegalArgumentException("增加数量必须在 1 到 100 之间");
        }
        UserEntity user = userService.getUserByToken(token);
        assertAdmin(user);
        if (mapper.addProtection(user.getId(), amount) == 0)
        {
            mapper.insertProtectionBalance(user.getId(), amount);
        }
        return protectionBalance(user.getId());
    }

    private void assertAdmin(UserEntity user)
    {
        if (!userService.hasAdminAuthority(user.getAuthority()))
        {
            throw new IllegalArgumentException("只有管理员可以使用连胜时光机");
        }
    }

    private int protectionBalance(Integer userId)
    {
        Integer balance = mapper.findProtectionBalance(userId);
        if (balance == null)
        {
            mapper.insertProtectionBalance(userId, 0);
            return 0;
        }
        return balance;
    }

    private void settleExpiredDays(Integer userId, LocalDate today)
    {
        List<StreakRecordEntity> allRecords = mapper.findAllByUser(userId);
        if (allRecords.isEmpty()) return;
        LocalDate cursor = allRecords.get(allRecords.size() - 1).getStudyDate().plusDays(1);
        while (cursor.isBefore(today))
        {
            if (mapper.findByUserAndDate(userId, cursor) == null)
            {
                String status = mapper.consumeProtection(userId) > 0 ? "protected" : "missed";
                mapper.insert(new StreakRecordEntity(null, userId, cursor, status));
            }
            cursor = cursor.plusDays(1);
        }
    }
}
