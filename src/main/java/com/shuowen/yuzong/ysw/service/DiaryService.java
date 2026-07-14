package com.shuowen.yuzong.ysw.service;

import com.shuowen.yuzong.util.ext.list.ListTool;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.ysw.data.domain.diary.DiaryCatalog;
import com.shuowen.yuzong.ysw.data.domain.diary.DiaryDigest;
import com.shuowen.yuzong.ysw.data.domain.diary.DiaryText;
import com.shuowen.yuzong.ysw.data.mapper.diary.DiaryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DiaryService
{
    @Autowired
    private DiaryMapper m;

    public DiaryCatalog getCatalog()
    {
        return new DiaryCatalog(m.getCatalog());
    }

    public List<DiaryDigest> query(Language l, Integer year, Integer month, LocalDate startDate, LocalDate endDate, Integer limit)
    {
        checkMonth(month);
        checkDateRange(startDate, endDate);
        return ListTool.mapping(
                m.query(year, month, startDate, endDate, normalizeLimit(limit)),
                item -> new DiaryDigest(item, l)
        );
    }

    public List<DiaryDigest> getRecent(Language l, Integer limit)
    {
        return ListTool.mapping(m.getRecent(normalizeLimit(limit)), item -> new DiaryDigest(item, l));
    }

    public Maybe<DiaryText> getDiaryById(Integer id, Language l)
    {
        return Maybe.uncertain(m.getDiaryById(id)).handleIfExist(item -> new DiaryText(item, l));
    }

    public Maybe<DiaryText> getDiaryByDate(LocalDate date, Language l)
    {
        return Maybe.uncertain(m.getDiaryByDate(date)).handleIfExist(item -> new DiaryText(item, l));
    }

    private Integer normalizeLimit(Integer limit)
    {
        if (limit == null) return 20;
        if (limit < 1) throw new IllegalArgumentException("limit 不能小于 1");
        return Math.min(limit, 100);
    }

    private void checkMonth(Integer month)
    {
        if (month == null) return;
        if (month < 1 || month > 12) throw new IllegalArgumentException("month 必须在 1-12 之间");
    }

    private void checkDateRange(LocalDate startDate, LocalDate endDate)
    {
        if (startDate == null || endDate == null) return;
        if (startDate.isAfter(endDate)) throw new IllegalArgumentException("startDate 不能晚于 endDate");
    }
}
