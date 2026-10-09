package com.shuowen.yuzong.study.streak;

import com.shuowen.yuzong.study.streak.data.StreakOverview;
import com.shuowen.yuzong.study.streak.data.StreakRecordUpdateRequest;
import com.shuowen.yuzong.study.streak.data.StreakUserSummary;
import com.shuowen.yuzong.util.tuple.APIResponse;
import com.shuowen.yuzong.util.tuple.HTTPCode;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/admin/study/streak")
public class StreakAdminController
{
    private final StreakAdminService service;

    public StreakAdminController(StreakAdminService service)
    {
        this.service = service;
    }

    @GetMapping("/users")
    public APIResponse<List<StreakUserSummary>> searchUsers(@RequestParam String t,
                                                            @RequestParam String keyword)
    {
        try { return APIResponse.success(service.searchUsers(t, keyword)); }
        catch (Exception e) { return failure(e); }
    }

    @GetMapping("/{userId}/overview")
    public APIResponse<StreakOverview> overview(@RequestParam String t,
                                                @PathVariable Integer userId,
                                                @RequestParam LocalDate from,
                                                @RequestParam LocalDate to)
    {
        try { return APIResponse.success(service.getOverview(t, userId, from, to)); }
        catch (Exception e) { return failure(e); }
    }

    @PatchMapping("/{userId}/record")
    public APIResponse<Void> updateRecord(@RequestParam String t,
                                          @PathVariable Integer userId,
                                          @RequestBody StreakRecordUpdateRequest request)
    {
        try
        {
            service.updateRecord(t, userId, request);
            return APIResponse.success();
        }
        catch (Exception e) { return failure(e); }
    }

    private <T> APIResponse<T> failure(Exception exception)
    {
        if (exception instanceof NoSuchElementException)
        {
            return APIResponse.failure(HTTPCode.NotLogin, errorMessage(exception));
        }
        if (exception instanceof SecurityException)
        {
            return APIResponse.failure(HTTPCode.NoPermission, errorMessage(exception));
        }
        return APIResponse.failure(errorMessage(exception));
    }

    private String errorMessage(Exception e)
    {
        e.printStackTrace();
        String message = e.getMessage();
        return message == null || message.isBlank() ? e.getClass().getSimpleName() : message;
    }
}
