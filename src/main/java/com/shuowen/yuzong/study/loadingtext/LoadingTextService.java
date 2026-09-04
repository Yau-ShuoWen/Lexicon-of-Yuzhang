package com.shuowen.yuzong.study.loadingtext;

import com.shuowen.yuzong.study.loadingtext.data.LoadingTextEntity;
import com.shuowen.yuzong.study.loadingtext.data.LoadingTextBatchItem;
import com.shuowen.yuzong.study.loadingtext.data.LoadingTextUpdate;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.core.Language;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional(rollbackFor = Exception.class)
public class LoadingTextService
{
    private final LoadingTextMapper mapper;

    public LoadingTextService(LoadingTextMapper mapper)
    {
        this.mapper = mapper;
    }

    /**
     * 根据当前方言随机获取一条提示语。空 tag 表示对所有方言有效。
     */
    public String randomText(Language language, Dialect dialect)
    {
        List<LoadingTextEntity> candidates = mapper.findAll().stream()
                .filter(entity -> isCandidate(entity, dialect))
                .toList();

        if (candidates.isEmpty()) return "";

        LoadingTextEntity entity = candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
        return LoadingTextUpdate.readTip(entity.getTip()).get(language).toString();
    }

    public List<LoadingTextUpdate> findAll()
    {
        return mapper.findAll().stream().map(LoadingTextUpdate::new).toList();
    }

    public LoadingTextUpdate findById(Integer id)
    {
        LoadingTextEntity entity = mapper.findById(id);
        if (entity == null) throw new IllegalArgumentException("没有找到这条加载提示语");
        return new LoadingTextUpdate(entity);
    }

    public void save(LoadingTextUpdate edit)
    {
        if (edit == null) throw new IllegalArgumentException("提交内容不能为空");

        LoadingTextEntity entity = edit.toEntity();
        if (entity.getId() == null || entity.getId() <= 0) mapper.insert(entity);
        else mapper.update(entity);
    }

    /**
     * 统一提交编辑页的全部草稿。校验通过后才开始写库，整个操作由事务保证原子性。
     */
    public void saveBatch(List<LoadingTextBatchItem> items)
    {
        if (items == null) throw new IllegalArgumentException("提交内容不能为空");

        List<Integer> deleting = new ArrayList<>();
        List<LoadingTextEntity> saving = new ArrayList<>();

        for (LoadingTextBatchItem item : items)
        {
            if (item == null) throw new IllegalArgumentException("提交内容中存在空行");

            if (item.isDeleted())
            {
                if (item.getId() != null && item.getId() > 0) deleting.add(item.getId());
            }
            else
            {
                // 先全部转换和校验，避免前面已经写入后才发现后面的行有问题。
                saving.add(item.toEntity());
            }
        }

        deleting.forEach(mapper::delete);
        for (LoadingTextEntity entity : saving)
        {
            if (entity.getId() == null || entity.getId() <= 0) mapper.insert(entity);
            else mapper.update(entity);
        }
    }

    private boolean appliesTo(LoadingTextEntity entity, Dialect dialect)
    {
        try
        {
            List<Dialect> tags = LoadingTextUpdate.readTag(entity.getTag());
            return tags.isEmpty() || tags.contains(dialect);
        }
        catch (RuntimeException ignored)
        {
            // 单条脏数据不能阻塞其他提示语和题目加载。
            return false;
        }
    }

    private boolean isCandidate(LoadingTextEntity entity, Dialect dialect)
    {
        try
        {
            if (!appliesTo(entity, dialect)) return false;
            LoadingTextUpdate.readTip(entity.getTip());
            return true;
        }
        catch (RuntimeException ignored)
        {
            return false;
        }
    }
}
