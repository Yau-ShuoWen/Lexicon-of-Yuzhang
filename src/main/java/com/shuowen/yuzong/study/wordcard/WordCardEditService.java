package com.shuowen.yuzong.study.wordcard;

import com.shuowen.yuzong.util.core.Dialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class WordCardEditService
{
    @Autowired
    private WordCardMapper mapper;

    @Autowired
    private ChapterWordCardMapper chapterWordCardMapper;

    public List<WordCardUpdate> findAll(Dialect dialect)
    {
        return mapper.findAll(dialect.toString()).stream()
                .map(item -> new WordCardUpdate(item, dialect))
                .toList();
    }

    public WordCardUpdate findById(Integer id, Dialect dialect)
    {
        WordCardEntity entity = mapper.findById(id, dialect.toString());
        if (entity == null) throw new IllegalArgumentException("没有找到词卡");
        return new WordCardUpdate(entity, dialect);
    }

    public WordCardUpdate save(WordCardUpdate update, Dialect dialect)
    {
        WordCardEntity entity = update.checkAndTransfer(dialect);
        if (entity.id == null || entity.id <= 0) mapper.insert(entity);
        else mapper.update(entity);
        return new WordCardUpdate(mapper.findById(entity.id, dialect.toString()), dialect);
    }

    public void delete(Integer id, Dialect dialect)
    {
        chapterWordCardMapper.deleteByWordCardId(id);
        mapper.delete(id, dialect.toString());
    }
}
