package com.shuowen.yuzong.dict.hanzi.service;

import com.shuowen.yuzong.dict.data.domain.Word.CiyuItem;
import com.shuowen.yuzong.dict.data.mapper.Word.CiyuMapper;
import com.shuowen.yuzong.dict.data.model.Word.CiyuEntity;
import com.shuowen.yuzong.dict.hanzi.domain.HanziWordUsage;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.text.ScTcChar;
import com.shuowen.yuzong.util.text.UString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class HanziWordService
{
    @Autowired
    private CiyuMapper words;

    public List<HanziWordUsage> findUsages(ScTcChar hanzi, Language language, Dialect dialect)
    {
        List<HanziWordUsage> result = new ArrayList<>();
        for (CiyuEntity entity : words.findCiyuContainingHanzi(
                hanzi.getSc().toString(), hanzi.getTc().toString(), dialect.toString()))
        {
            CiyuItem item = CiyuItem.of(entity, language);
            Set<Integer> indexes = new LinkedHashSet<>();
            collectIndexes(entity.getSc(), hanzi.getSc().toString(), indexes);
            collectIndexes(entity.getTc(), hanzi.getTc().toString(), indexes);

            for (Integer index : indexes)
            {
                if (index < item.getMainPy().size())
                    result.add(new HanziWordUsage(item, index, dialect));
            }
        }
        return result;
    }

    private void collectIndexes(String word, String hanzi, Set<Integer> indexes)
    {
        if (word == null || hanzi == null || hanzi.isEmpty()) return;
        UString text = UString.of(word);
        for (int index = 0; index < text.length(); index++)
            if (text.at(index).equals(hanzi)) indexes.add(index);
    }
}
