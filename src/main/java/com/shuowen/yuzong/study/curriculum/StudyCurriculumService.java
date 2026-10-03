package com.shuowen.yuzong.study.curriculum;

import com.shuowen.yuzong.study.curriculum.data.StudyLevelEntity;
import com.shuowen.yuzong.study.curriculum.data.StudyLevelUpdate;
import com.shuowen.yuzong.study.curriculum.data.StudyChapterCatalog;
import com.shuowen.yuzong.study.curriculum.data.StudyChapterEntity;
import com.shuowen.yuzong.study.curriculum.data.StudyChapterUpdate;
import com.shuowen.yuzong.study.curriculum.data.StudyPartCatalog;
import com.shuowen.yuzong.study.curriculum.data.StudyPartEntity;
import com.shuowen.yuzong.study.curriculum.data.StudyPartUpdate;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.core.Language;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class StudyCurriculumService
{
    private final StudyPartMapper partMapper;
    private final StudyLevelMapper levelMapper;
    private final StudyChapterMapper chapterMapper;

    public StudyCurriculumService(StudyPartMapper partMapper, StudyLevelMapper levelMapper,
                                  StudyChapterMapper chapterMapper)
    {
        this.partMapper = partMapper;
        this.levelMapper = levelMapper;
        this.chapterMapper = chapterMapper;
    }

    public List<StudyPartCatalog> getCatalog(Language language, Dialect dialect)
    {
        return partMapper.findAll(dialect.toString()).stream()
                .map(part -> toPartCatalog(part))
                .toList();
    }

    public List<StudyPartCatalog> findAllForManage(Dialect dialect)
    {
        return partMapper.findAll(dialect.toString()).stream()
                .map(part -> toPartCatalog(part))
                .toList();
    }

    private StudyPartCatalog toPartCatalog(StudyPartEntity part)
    {
        return new StudyPartCatalog(part, chapterMapper.countByPartId(part.getId()) > 0);
    }

    public List<StudyChapterCatalog> findChapters(Integer partId, Dialect dialect)
    {
        requirePart(partId, dialect);
        return chapterMapper.findByPartId(partId).stream().map(StudyChapterCatalog::new).toList();
    }

    public List<StudyChapterCatalog> saveChapters(Integer partId, List<StudyChapterUpdate> updates,
                                                   Dialect dialect)
    {
        requirePart(partId, dialect);
        if (updates == null) throw new IllegalArgumentException("章节列表不能为空");
        if (updates.stream().anyMatch(item -> item == null))
            throw new IllegalArgumentException("章节内容不能为空");

        List<StudyChapterEntity> existing = chapterMapper.findByPartId(partId);
        Set<Integer> existingIds = existing.stream().map(StudyChapterEntity::getId).collect(Collectors.toSet());
        List<Integer> submittedIdList = updates.stream().map(StudyChapterUpdate::getId)
                .filter(id -> id != null && id > 0).toList();
        Set<Integer> submittedIds = Set.copyOf(submittedIdList);
        if (!submittedIds.equals(existingIds))
            throw new IllegalArgumentException("当前章节暂时不允许删除");
        if (submittedIds.size() != submittedIdList.size())
            throw new IllegalArgumentException("章节编号不能重复");

        for (int index = 0; index < existing.size(); index++)
            chapterMapper.updateOrder(existing.get(index).getId(), partId, -(index + 1));

        for (int index = 0; index < updates.size(); index++)
        {
            StudyChapterUpdate update = updates.get(index);
            update.setChapterOrder(index + 1);
            StudyChapterEntity entity = update.toEntity(partId);
            if (entity.getId() == null || entity.getId() <= 0) chapterMapper.insert(entity);
            else chapterMapper.update(entity);
        }
        return findChapters(partId, dialect);
    }

    public void deleteChapter(Integer partId, Integer chapterId, Dialect dialect)
    {
        requirePart(partId, dialect);
        boolean exists = chapterMapper.findByPartId(partId).stream()
                .anyMatch(chapter -> chapter.getId().equals(chapterId));
        if (!exists) throw new IllegalArgumentException("章节不存在");
        if (chapterMapper.delete(chapterId, partId) != 1)
            throw new IllegalArgumentException("章节删除失败");
    }

    /**
     * 保存一个方言下的完整部分列表。
     *
     * <p>拖动排序必须整体提交。先将旧顺序移动到临时负数区间，避免交换两个
     * 部分时触发 (dialect, part_order) 唯一索引冲突。当前阶段不允许通过省略
     * 已有项目来删除部分。</p>
     */
    public List<StudyPartCatalog> saveParts(List<StudyPartUpdate> updates, Dialect dialect)
    {
        if (updates == null) throw new IllegalArgumentException("部分列表不能为空");
        if (updates.stream().anyMatch(item -> item == null))
            throw new IllegalArgumentException("部分内容不能为空");

        List<StudyPartEntity> existing = partMapper.findAll(dialect.toString());
        Set<Integer> existingIds = existing.stream().map(StudyPartEntity::getId).collect(Collectors.toSet());
        Set<Integer> submittedIds = updates.stream()
                .map(StudyPartUpdate::getId)
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toSet());

        if (!submittedIds.equals(existingIds))
            throw new IllegalArgumentException("当前部分暂时不允许删除");
        if (submittedIds.size() != updates.stream().filter(item -> item.getId() != null && item.getId() > 0).count())
            throw new IllegalArgumentException("部分编号不能重复");

        for (int index = 0; index < existing.size(); index++)
            partMapper.updateOrder(existing.get(index).getId(), dialect.toString(), -(index + 1));

        for (int index = 0; index < updates.size(); index++)
        {
            StudyPartUpdate update = updates.get(index);
            update.setPartOrder(index + 1);
            StudyPartEntity entity = update.toEntity(dialect);
            if (entity.getId() == null || entity.getId() <= 0) partMapper.insert(entity);
            else partMapper.update(entity);
        }

        return findAllForManage(dialect);
    }

    public void deletePart(Integer id, Dialect dialect)
    {
        StudyPartEntity part = partMapper.findById(id, dialect.toString());
        if (part == null) throw new IllegalArgumentException("部分不存在");
        if (chapterMapper.countByPartId(id) > 0)
            throw new IllegalArgumentException("部分中已有章节，不能删除");
        if (partMapper.delete(id, dialect.toString()) != 1)
            throw new IllegalArgumentException("部分删除失败");
    }

    public StudyPartUpdate findPart(Integer id, Dialect dialect)
    {
        StudyPartEntity entity = partMapper.findById(id, dialect.toString());
        if (entity == null) throw new IllegalArgumentException("没有找到这个部分");
        return new StudyPartUpdate(entity);
    }

    public StudyPartUpdate savePart(StudyPartUpdate update, Dialect dialect)
    {
        if (update == null) throw new IllegalArgumentException("部分内容不能为空");

        StudyPartEntity entity = update.toEntity(dialect);
        if (entity.getId() == null || entity.getId() <= 0)
        {
            partMapper.insert(entity);
        }
        else
        {
            if (partMapper.findById(entity.getId(), dialect.toString()) == null)
                throw new IllegalArgumentException("部分不存在，不能编辑");
            partMapper.update(entity);
        }
        return new StudyPartUpdate(partMapper.findById(entity.getId(), dialect.toString()));
    }

    public List<StudyLevelUpdate> findLevels(Integer partId, Dialect dialect)
    {
        requirePart(partId, dialect);
        return levelMapper.findByPartId(partId).stream().map(StudyLevelUpdate::new).toList();
    }

    public StudyLevelUpdate findLevel(Integer id, Dialect dialect)
    {
        StudyLevelEntity entity = levelMapper.findById(id, dialect.toString());
        if (entity == null) throw new IllegalArgumentException("没有找到这个关卡");
        return new StudyLevelUpdate(entity);
    }

    public StudyLevelUpdate saveLevel(StudyLevelUpdate update, Dialect dialect)
    {
        if (update == null) throw new IllegalArgumentException("关卡内容不能为空");
        requirePart(update.getPartId(), dialect);

        StudyLevelEntity entity = update.toEntity();
        if (entity.getId() == null || entity.getId() <= 0)
        {
            levelMapper.insert(entity);
        }
        else
        {
            if (levelMapper.findById(entity.getId(), dialect.toString()) == null)
                throw new IllegalArgumentException("关卡不存在，不能编辑");
            levelMapper.update(entity);
        }
        return new StudyLevelUpdate(levelMapper.findById(entity.getId(), dialect.toString()));
    }

    private void requirePart(Integer partId, Dialect dialect)
    {
        if (partId == null || partMapper.findById(partId, dialect.toString()) == null)
            throw new IllegalArgumentException("部分不存在，不能操作关卡");
    }
}
