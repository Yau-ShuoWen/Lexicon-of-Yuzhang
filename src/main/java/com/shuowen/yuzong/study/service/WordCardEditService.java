package com.shuowen.yuzong.study.service;

import com.shuowen.yuzong.study.data.domain.WordCardUpdate;
import com.shuowen.yuzong.study.data.mapper.WordCardMapper;
import com.shuowen.yuzong.study.data.model.WordCardEntity;
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

    public void save(WordCardUpdate update, Dialect dialect)
    {
        WordCardEntity entity = update.checkAndTransfer(dialect);
        if (entity.id == null || entity.id <= 0) mapper.insert(entity);
        else mapper.update(entity);
    }

    public void delete(Integer id, Dialect dialect)
    {
        mapper.delete(id, dialect.toString());
    }
}
