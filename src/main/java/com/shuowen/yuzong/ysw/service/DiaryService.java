package com.shuowen.yuzong.ysw.service;

import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.ext.list.ListTool;
import com.shuowen.yuzong.util.tuple.Maybe;
import com.shuowen.yuzong.util.tuple.Twin;
import com.shuowen.yuzong.ysw.data.domain.diary.DiaryCatalog;
import com.shuowen.yuzong.ysw.data.domain.diary.DiaryDigest;
import com.shuowen.yuzong.ysw.data.domain.diary.DiaryEditVisibility;
import com.shuowen.yuzong.ysw.data.domain.diary.DiaryText;
import com.shuowen.yuzong.ysw.data.domain.diary.DiaryViewMode;
import com.shuowen.yuzong.ysw.data.dto.diary.DiaryEditData;
import com.shuowen.yuzong.ysw.data.dto.diary.DiaryEditRequest;
import com.shuowen.yuzong.ysw.data.mapper.diary.DiaryMapper;
import com.shuowen.yuzong.ysw.data.model.diary.DiaryCatalogEntity;
import com.shuowen.yuzong.ysw.data.model.diary.DiaryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DiaryService
{
    @Autowired
    private DiaryMapper m;

    public DiaryCatalog getCatalog()
    {
        return getCatalog(DiaryViewMode.STRANGER, DiaryViewMode.STRANGER);
    }

    public DiaryCatalog getCatalog(DiaryViewMode requestedView, DiaryViewMode allowedView)
    {
        var view = DiaryViewMode.clamp(requestedView, allowedView);
        return new DiaryCatalog(m.getCatalog(view.name()));
    }

    public List<DiaryDigest> query(Language l, Integer year, Integer month)
    {
        return query(l, year, month, DiaryViewMode.STRANGER, DiaryViewMode.STRANGER);
    }

    public List<DiaryDigest> query(Language l, Integer year, Integer month, DiaryViewMode requestedView, DiaryViewMode allowedView)
    {
        if ((year == null) != (month == null))
        {
            throw new IllegalArgumentException("year 和 month 必须同时传或同时不传");
        }

        var view = DiaryViewMode.clamp(requestedView, allowedView);

        if (year == null)
        {
            return getRecent(l, view, view);
        }

        return ListTool.mapping(
                m.query(year, month, view.name()),
                item -> new DiaryDigest(item, l, resolveBody(item, view))
        );
    }

    public List<DiaryDigest> getRecent(Language l)
    {
        return getRecent(l, DiaryViewMode.STRANGER, DiaryViewMode.STRANGER);
    }

    public List<DiaryDigest> getRecent(Language l, DiaryViewMode requestedView, DiaryViewMode allowedView)
    {
        var view = DiaryViewMode.clamp(requestedView, allowedView);
        return ListTool.mapping(m.getRecent(view.name()), item -> new DiaryDigest(item, l, resolveBody(item, view)));
    }

    public DiaryEditData getForEdit(Integer id)
    {
        var diary = m.getDiaryById(id);
        if (diary == null)
        {
            throw new IllegalArgumentException("日记不存在");
        }
        return DiaryEditData.of(diary);
    }

    public DiaryEditData updateForEdit(Integer id, DiaryEditRequest request)
    {
        if (request == null)
        {
            throw new IllegalArgumentException("编辑内容不能为空");
        }
        if (request.date() == null)
        {
            throw new IllegalArgumentException("日期不能为空");
        }
        if (request.sort() == null || request.sort() <= 0)
        {
            throw new IllegalArgumentException("sort 必须大于 0");
        }
        if (request.content() == null)
        {
            throw new IllegalArgumentException("正文不能为空");
        }

        var visibility = DiaryEditVisibility.of(request.visibility());
        var diary = m.getDiaryById(id);
        if (diary == null)
        {
            throw new IllegalArgumentException("日记不存在");
        }

        diary.setDate(request.date());
        diary.setSort(request.sort());
        diary.setContent(request.content());
        diary.setStartDate(request.startDate());
        diary.setFinalizeDate(request.finalizeDate());
        diary.setForFriend(visibility == DiaryEditVisibility.PRIVATE ? null : request.forFriend());
        diary.setForStranger(visibility == DiaryEditVisibility.STRANGER ? request.forStranger() : null);

        if (m.updateForEdit(diary) != 1)
        {
            throw new IllegalStateException("日记保存失败");
        }
        return DiaryEditData.of(diary);
    }

    public Maybe<DiaryText> getDiaryById(Integer id, Language l)
    {
        return getDiaryById(id, l, DiaryViewMode.STRANGER, DiaryViewMode.STRANGER);
    }

    public Maybe<DiaryText> getDiaryById(Integer id, Language l, DiaryViewMode requestedView, DiaryViewMode allowedView)
    {
        var item = m.getDiaryById(id);
        if (item == null)
        {
            return Maybe.nothing();
        }

        var availableViews = getAvailableViews(item);
        var view = resolveEffectiveView(requestedView, allowedView, availableViews);
        if (view == null)
        {
            return Maybe.nothing();
        }

        return Maybe.exist(new DiaryText(item, l, resolveBody(item, view), view.name().toLowerCase(), availableViews));
    }

    public Twin<Maybe<Map>> getNearby(Integer id)
    {
        return getNearby(id, DiaryViewMode.STRANGER, DiaryViewMode.STRANGER);
    }

    public Twin<Maybe<Map>> getNearby(Integer id, DiaryViewMode requestedView, DiaryViewMode allowedView)
    {
        var item = m.getDiaryById(id);
        if (item == null)
        {
            return Twin.of(Maybe.nothing(), Maybe.nothing());
        }

        var availableViews = getAvailableViews(item);
        var view = resolveEffectiveView(requestedView, allowedView, availableViews);
        if (view == null)
        {
            return Twin.of(Maybe.nothing(), Maybe.nothing());
        }

        Maybe<Map> prev = Maybe.uncertain(m.selectPrev(id, view.name())).handleIfExist(this::toNearbyMap);
        Maybe<Map> next = Maybe.uncertain(m.selectNext(id, view.name())).handleIfExist(this::toNearbyMap);

        return Twin.of(prev, next);
    }

    private String resolveBody(DiaryEntity item, DiaryViewMode view)
    {
        if (item == null)
        {
            return null;
        }

        return switch (view)
        {
            case SELF -> item.getContent();
            case FRIEND -> item.getForFriend() != null ? item.getForFriend() : item.getForStranger();
            case STRANGER -> item.getForStranger();
        };
    }

    private List<String> getAvailableViews(DiaryEntity item)
    {
        List<String> views = new ArrayList<>(3);
        if (item.getContent() != null)
        {
            views.add(DiaryViewMode.SELF.name().toLowerCase());
        }
        if (item.getForFriend() != null)
        {
            views.add(DiaryViewMode.FRIEND.name().toLowerCase());
        }
        if (item.getForStranger() != null)
        {
            views.add(DiaryViewMode.STRANGER.name().toLowerCase());
        }
        return views;
    }

    private DiaryViewMode resolveEffectiveView(DiaryViewMode requestedView, DiaryViewMode allowedView, List<String> availableViews)
    {
        List<DiaryViewMode> candidates = new ArrayList<>();
        for (String view : availableViews)
        {
            var mode = DiaryViewMode.of(view);
            if (rank(mode) <= rank(allowedView))
            {
                candidates.add(mode);
            }
        }

        if (candidates.isEmpty())
        {
            return null;
        }

        var requested = DiaryViewMode.clamp(requestedView, allowedView);
        if (candidates.contains(requested))
        {
            return requested;
        }

        return candidates.stream()
                .min((left, right) -> {
                    int leftDistance = Math.abs(rank(left) - rank(requested));
                    int rightDistance = Math.abs(rank(right) - rank(requested));
                    if (leftDistance != rightDistance)
                    {
                        return Integer.compare(leftDistance, rightDistance);
                    }
                    return Integer.compare(rank(right), rank(left));
                })
                .orElse(candidates.get(0));
    }

    private int rank(DiaryViewMode view)
    {
        return switch (view)
        {
            case SELF -> 3;
            case FRIEND -> 2;
            case STRANGER -> 1;
        };
    }

    private Map<String, Object> toNearbyMap(DiaryEntity item)
    {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", item.getId());
        map.put("date", item.getDate());
        map.put("sort", item.getSort());
        return map;
    }
}
