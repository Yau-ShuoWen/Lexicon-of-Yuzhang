package com.shuowen.yuzong.service.impl.Reference;

import com.shuowen.yuzong.Tool.JavaUtilExtend.SetTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.UniqueList;
import com.shuowen.yuzong.util.text.ScTcText;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinConfig;
import com.shuowen.yuzong.data.domain.Reference.DictCode;
import com.shuowen.yuzong.data.domain.Reference.RefItem;
import com.shuowen.yuzong.data.mapper.Reference.RefMapper;
import com.shuowen.yuzong.data.model.Reference.RefEntity;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional (rollbackFor = {Exception.class})
public class RefReadService
{
    @Autowired
    private RefMapper ck;

    public List<RefItem> getAllRef(ScTcText query, final PinyinConfig data)
    {
        Set<String> dictGroup = SetTool.mapping(data.getDictGroup().getKeySet(), DictCode::toString);

        UniqueList<RefEntity, Integer> ulist = UniqueList.of(RefEntity::getId);
        ulist.addAll(ck.findAllByQuery(dictGroup, query.getSc().toString()));
        ulist.addAll(ck.findAllByQuery(dictGroup, query.getTc().toString()));

        var list = ulist.getList();

        list.sort(Comparator.comparingInt(ref -> Math.min(
                        ref.getContent().indexOf(query.getSc().toString()),
                        ref.getContent().indexOf(query.getTc().toString())
                )
        ));

        List<RefItem> ans = new ArrayList<>();
        for (RefEntity ref : list) ans.add(new RefItem(ref, data));

        return ans;
    }

    private static RefReadService instance;

    @PostConstruct
    public void init()
    {
        instance = this;
    }

    public static List<RefItem> getRef(ScTcText query, final PinyinConfig data)
    {
        return instance.getAllRef(query, data);
    }
}
