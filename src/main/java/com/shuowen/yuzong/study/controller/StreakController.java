package com.shuowen.yuzong.study.controller;

import com.shuowen.yuzong.study.data.dto.StreakOverview;
import com.shuowen.yuzong.study.service.StreakService;
import com.shuowen.yuzong.util.tuple.APIResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/study/streak")
public class StreakController
{
    @Autowired
    private StreakService service;

    @GetMapping
    public APIResponse<StreakOverview> overview(
            @RequestParam String t,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to
    )
    {
        try
        {
            LocalDate end = to == null ? LocalDate.now().plusDays(6) : to;
            LocalDate start = from == null ? LocalDate.now().minusDays(14) : from;
            if (start.isAfter(end)) throw new IllegalArgumentException("日期范围无效");
            return APIResponse.success(service.getOverview(t, start, end));
        }
        catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }

    @PostMapping("/activity/complete")
    public APIResponse<Void> complete(@RequestParam String t)
    {
        try { service.markStudyCompleted(t); return APIResponse.success(); }
        catch (Exception e) { return APIResponse.failure(e.getMessage()); }
    }

}
