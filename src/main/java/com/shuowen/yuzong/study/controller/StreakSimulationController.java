package com.shuowen.yuzong.study.controller;

import com.shuowen.yuzong.study.data.dto.StreakOverview;
import com.shuowen.yuzong.study.service.StreakSimulationService;
import com.shuowen.yuzong.util.tuple.APIResponse;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/study/streak/simulation")
public class StreakSimulationController
{
    private final StreakSimulationService service;

    public StreakSimulationController(StreakSimulationService service)
    {
        this.service = service;
    }

    @GetMapping
    public APIResponse<StreakOverview> overview(@RequestParam String t,
                                                @RequestParam LocalDate simulatedToday,
                                                @RequestParam LocalDate from,
                                                @RequestParam LocalDate to)
    {
        try
        {
            if (from.isAfter(to)) throw new IllegalArgumentException("日期范围无效");
            return APIResponse.success(service.getOverview(t, from, to, simulatedToday));
        }
        catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }

    @PostMapping("/{status}")
    public APIResponse<Void> mark(@RequestParam String t,
                                  @RequestParam LocalDate date,
                                  @PathVariable String status)
    {
        try
        {
            service.mark(t, date, status);
            return APIResponse.success();
        }
        catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }

    @PostMapping("/activity/complete")
    public APIResponse<Void> completeActivity(@RequestParam String t,
                                               @RequestParam LocalDate date)
    {
        try
        {
            service.completeActivity(t, date);
            return APIResponse.success();
        }
        catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }

    @DeleteMapping
    public APIResponse<Void> clear(@RequestParam String t)
    {
        try
        {
            service.clear(t);
            return APIResponse.success();
        }
        catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }

    @PostMapping("/protection/add")
    public APIResponse<Integer> addProtection(@RequestParam String t,
                                              @RequestParam(defaultValue = "1") int amount)
    {
        try
        {
            return APIResponse.success(service.addProtection(t, amount));
        }
        catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }
}
