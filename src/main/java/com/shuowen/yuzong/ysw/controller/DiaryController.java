package com.shuowen.yuzong.ysw.controller;

import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.tuple.Maybe;
import com.shuowen.yuzong.util.tuple.Twin;
import com.shuowen.yuzong.user.service.UserService;
import com.shuowen.yuzong.ysw.data.domain.diary.DiaryCatalog;
import com.shuowen.yuzong.ysw.data.domain.diary.DiaryDigest;
import com.shuowen.yuzong.ysw.data.domain.diary.DiaryText;
import com.shuowen.yuzong.ysw.data.domain.diary.DiaryViewMode;
import com.shuowen.yuzong.ysw.service.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Diary 模块的只读接口。
 * <p>
 * 前端可以直接把这个类当作接口说明来使用：
 * 1. 所有接口都挂在 /api/diary/ 下面。
 * 2. 目前全部是 GET 接口，适合先完成目录页、列表页、详情页。
 * 3. 带 {l} 的接口表示需要传入语言参数，交给后端做简繁转换。
 * 4. 日期参数统一使用 yyyy-MM-dd 格式。
 * <p>
 * 当前建议的前端页面拆分：
     * 1. 目录页：调用 /catalog
     * 2. 列表页：调用 /query/{l}
     * 3. 最近更新区域：首页或侧栏调用 /recent/{l}
     * 4. 详情页：调用 /item/{l}/id/{id}
 */
@RestController
@RequestMapping ("/api/diary/")
public class DiaryController
{
    @Autowired
    private DiaryService s;

    @Autowired
    private UserService userService;

    /**
     * 获取日记目录归档。<br>
     * <br>
     * 用途：<br>
     * 1. 给前端左侧目录树、归档页、年份月份筛选器使用。<br>
     * 2. 返回结果已经按 年 -> 月 分组，并且按时间倒序排列。<br>
     * <br>
     * 请求示例：<br>
     * GET /api/diary/catalog<br>
     * <br>
     * 返回结构说明：<br>
     * 1. years: 年份列表<br>
     * 2. years[].year: 年份，例如 2026<br>
     * 3. years[].total: 这一年的日记总数<br>
     * 4. years[].months: 该年的月份列表<br>
     * 5. years[].months[].month: 月份，1-12<br>
     * 6. years[].months[].total: 该月日记篇数<br>
     * 7. years[].months[].startDate / endDate: 该月第一篇和最后一篇的日期<br>
     * <br>
     * 前端建议：<br>
     * 1. 目录树只需要首次加载一次，然后本地缓存即可。<br>
     * 2. 点击某个月后，再调用 /query/{l}?year=xxxx&month=xx 获取该月列表。
     */
    @GetMapping ("/catalog")
    public DiaryCatalog getCatalog(
            @RequestParam(required = false) String view,
            @RequestParam(required = false) String t
    )
    {
        var requested = DiaryViewMode.of(view);
        var allowed = resolveAllowedView(t);
        return s.getCatalog(requested, allowed);
    }

    /**
     * 按条件查询日记摘要列表。
     * <p>
     * 这是列表页最核心的接口，前端一般优先对接这个接口。
     * <p>
     * 请求示例：
     * 1. GET /api/diary/query/sc
     * 2. GET /api/diary/query/sc?year=2026&month=7
     * <p>
     * 路径参数：
     * 1. l: 语言参数，决定返回内容的简繁版本。
     * <p>
     * 查询参数：
     * 1. year: 可选，按年份过滤
     * 2. month: 可选，按月份过滤，范围 1-12
     * 3. year: 可选，按年份过滤
     * 4. month: 可选，按月份过滤，范围 1-12
     * <p>
     * 过滤规则：
     * 1. year 和 month 都可空。
     * 2. year 和 month 可以组合使用。
     * 3. 如果不传 year 和 month，则返回最近 20 篇日记，按日期从近到远排序。
     * 4. 如果传了 year 和 month，则返回对应年月的全部日记，按日期从早到晚排序。
     * <p>
     * 返回结构说明：
     * 1. 每一项都是 DiaryDigest，用于列表展示，不返回完整正文。
     * 2. date: 这篇日记对应的主题日期
     * 3. abridge: 摘要，类型是 Maybe<UString>；如果 empty=true 表示暂无摘要
     * 4. startDate / finalizeDate: 写作起止日期，可用于展示“写于/完成于”
     * 5. id: 主键，详情页跳转可直接使用
     * 6. updatedTime: 最后更新时间，可用于排序提示或“最近编辑”
     * <p>
     * 前端建议：
     * 1. 列表卡片先渲染 abridge，没有摘要时退回显示 date。
     * 2. 如果已经从 /catalog 选中了年月，优先传 year + month，不必自己切日期区间。
     * 3. 当前接口没有分页。
     */
    @GetMapping ("/query/{l}")
    public List<DiaryDigest> query(
            @PathVariable Language l,
            @RequestParam (required = false) Integer year,
            @RequestParam (required = false) Integer month,
            @RequestParam(required = false) String view,
            @RequestParam(required = false) String t
    )
    {
        var requested = DiaryViewMode.of(view);
        var allowed = resolveAllowedView(t);
        return s.query(l, year, month, requested, allowed);
    }

    /**
     * 获取最近若干篇日记摘要。
     * <p>
     * 用途：
     * 1. 首页“最近更新”
     * 2. 详情页侧栏“最近文章”
     * 3. 后台概览页
     * <p>
     * 请求示例：
     * GET /api/diary/recent/sc
     * <p>
     * 参数说明：
     * 1. l: 语言参数
     * 2. 默认返回 20 篇
     * <p>
     * 返回值：
     * 与 /query/{l} 相同，都是 DiaryDigest 列表。
     * <p>
     * 前端建议：
     * 1. 这个接口适合轻量展示，不建议把它当成完整列表页接口长期使用。
     */
    @GetMapping ("/recent/{l}")
    public List<DiaryDigest> recent(
            @PathVariable Language l,
            @RequestParam(required = false) String view,
            @RequestParam(required = false) String t
    )
    {
        var requested = DiaryViewMode.of(view);
        var allowed = resolveAllowedView(t);
        return s.getRecent(l, requested, allowed);
    }

    /**
     * 按数据库主键查询完整日记。
     * <p>
     * 请求示例：
     * GET /api/diary/item/sc/id/123
     * <p>
     * 用途：
     * 1. 从列表页点击某一项进入详情页
     * 2. 适合前端内部跳转，因为列表接口已经能拿到 id
     * <p>
     * 返回结构说明：
     * 1. 返回 Maybe<DiaryText>
     * 2. 如果 empty=false，value 就是完整日记内容
     * 3. 如果 empty=true，说明该 id 不存在
     * <p>
     * DiaryText 关键字段：
     * 1. date: 主题日期
     * 2. content: 正文，可能为空
     * 3. abridge: 摘要，可能为空
     * 4. startDate / finalizeDate: 写作时间信息
     * 5. id / createdTime / updatedTime: 元信息
     * <p>
     * 前端建议：
     * 1. 不要假设一定查得到，需处理 empty=true 的空态页面。
     * 2. content 和 abridge 都是 Maybe，需要先判断 empty。
     */
    @GetMapping ("/item/{l}/id/{id}")
    public Maybe<DiaryText> getById(
            @PathVariable Language l,
            @PathVariable Integer id,
            @RequestParam(required = false) String view,
            @RequestParam(required = false) String t
    )
    {
        var requested = DiaryViewMode.of(view);
        var allowed = resolveAllowedView(t);
        return s.getDiaryById(id, l, requested, allowed);
    }

    @GetMapping ("/item/nearby/{id}")
    public Twin<Maybe<Map>> getNearby(
            @PathVariable Integer id,
            @RequestParam(required = false) String view,
            @RequestParam(required = false) String t
    )
    {
        var requested = DiaryViewMode.of(view);
        var allowed = resolveAllowedView(t);
        return s.getNearby(id, requested, allowed);
    }

    private DiaryViewMode resolveAllowedView(String token)
    {
        if (token == null || token.trim().isEmpty())
        {
            return DiaryViewMode.STRANGER;
        }

        try
        {
            var user = userService.getUserByToken(token);
            if (user == null)
            {
                return DiaryViewMode.STRANGER;
            }
            String authority = user.getAuthority();
            if (userService.canReadBlogPrivate(authority))
            {
                return DiaryViewMode.SELF;
            }
            if (userService.canReadBlogFriends(authority))
            {
                return DiaryViewMode.FRIEND;
            }
            if (userService.canReadBlogPublic(authority))
            {
                return DiaryViewMode.STRANGER;
            }
        }
        catch (Exception ignored)
        {
        }
        return DiaryViewMode.STRANGER;
    }
}
