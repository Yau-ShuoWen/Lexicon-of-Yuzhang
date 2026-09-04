package com.shuowen.yuzong.study.streak;

import com.shuowen.yuzong.study.streak.data.StreakOverview;
import com.shuowen.yuzong.study.streak.data.StreakRecord;
import com.shuowen.yuzong.study.streak.data.StreakRecordEntity;
import com.shuowen.yuzong.user.data.model.UserEntity;
import com.shuowen.yuzong.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class StreakShowService
{
    @Autowired
    private StreakMapper mapper;

    @Autowired
    private UserService userService;

    public StreakOverview getOverview(String token, LocalDate from, LocalDate to)
    {
        UserEntity user = userService.getUserByToken(token);
        return getOverviewForUser(user.getId(), from, to);
    }

    public StreakOverview getOverviewForUser(Integer userId, LocalDate from, LocalDate to)
    {
        if (userId == null) throw new IllegalArgumentException("用户编号不能为空");
        if (from == null || to == null || from.isAfter(to))
        {
            throw new IllegalArgumentException("日期范围无效");
        }
        LocalDate today = LocalDate.now();
        settleExpiredDays(userId, today);
        List<StreakRecordEntity> records = mapper.findByUserAndRange(userId, from, to);
        List<StreakRecordEntity> allRecords = mapper.findAllByUser(userId);
        return buildOverview(records, allRecords, today, protectionBalance(userId));
    }

    public void markStudyCompleted(String token)
    {
        String status = "completed";
        UserEntity user = userService.getUserByToken(token);
        LocalDate today = LocalDate.now();
        settleExpiredDays(user.getId(), today);
        StreakRecordEntity existing = mapper.findByUserAndDate(user.getId(), today);
        if (existing == null)
        {
            mapper.insert(new StreakRecordEntity(null, user.getId(), today, status));
        }
        else if (!"completed".equals(existing.getStatus()))
        {
            mapper.updateStatus(user.getId(), today, status);
        }
    }

    private boolean isCounted(StreakRecordEntity record)
    {
        return record != null && ("completed".equals(record.getStatus()) || "protected".equals(record.getStatus()));
    }

    private void validateStatus(String status)
    {
        if (!List.of("completed", "protected", "missed").contains(status))
        {
            throw new IllegalArgumentException("连胜标记无效");
        }
    }

    StreakOverview buildOverview(List<StreakRecordEntity> records,
                                 List<StreakRecordEntity> allRecords,
                                 LocalDate today,
                                 int protectionBalance)
    {
        Map<LocalDate, StreakRecordEntity> byDate = allRecords.stream()
                .collect(Collectors.toMap(StreakRecordEntity::getStudyDate, Function.identity(), (left, right) -> right));
        String todayStatus = byDate.get(today) == null ? null : byDate.get(today).getStatus();
        LocalDate cursor = isCounted(byDate.get(today)) ? today : today.minusDays(1);
        int currentStreak = 0;
        if ("missed".equals(todayStatus)) cursor = today.minusDays(1);
        while (isCounted(byDate.get(cursor)))
        {
            if ("completed".equals(byDate.get(cursor).getStatus())) currentStreak++;
            cursor = cursor.minusDays(1);
        }

        int longestStreak = 0;
        int running = 0;
        LocalDate previous = null;
        boolean previousCounted = false;
        for (StreakRecordEntity record : allRecords)
        {
            boolean contiguous = previous != null && previous.plusDays(1).equals(record.getStudyDate());
            if ("completed".equals(record.getStatus())) running = contiguous && previousCounted ? running + 1 : 1;
            else if ("protected".equals(record.getStatus())) running = contiguous && previousCounted ? running : 0;
            else running = 0;
            longestStreak = Math.max(longestStreak, running);
            previous = record.getStudyDate();
            previousCounted = isCounted(record);
        }

        int completedDays = (int) allRecords.stream().filter(item -> "completed".equals(item.getStatus())).count();
        int protectedDays = (int) allRecords.stream().filter(item -> "protected".equals(item.getStatus())).count();
        List<StreakRecord> result = records.stream()
                .map(item -> new StreakRecord(item.getStudyDate(), item.getStatus()))
                .toList();
        return new StreakOverview(currentStreak, longestStreak, completedDays,
                completedDays, protectedDays, protectionBalance, todayStatus, result);
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
                if (mapper.consumeProtection(userId) > 0)
                {
                    mapper.insert(new StreakRecordEntity(null, userId, cursor, "protected"));
                }
                else
                {
                    mapper.insert(new StreakRecordEntity(null, userId, cursor, "missed"));
                }
            }
            cursor = cursor.plusDays(1);
        }
    }
}
