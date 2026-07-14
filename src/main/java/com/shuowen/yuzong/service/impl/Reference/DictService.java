package com.shuowen.yuzong.service.impl.Reference;

import com.shuowen.yuzong.util.text.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.data.domain.Reference.DictCode;
import com.shuowen.yuzong.data.domain.Reference.DictGroup;
import com.shuowen.yuzong.data.mapper.Reference.DictMapper;
import com.shuowen.yuzong.data.mapper.Reference.RefMapper;
import com.shuowen.yuzong.data.model.Reference.DictEntity;
import com.shuowen.yuzong.data.model.Reference.RefEntity;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional (rollbackFor = {Exception.class})
public class DictService
{
    @Autowired
    private DictMapper cd;

    public static Set<DictEntity> getDicts(Dialect d)
    {
        return new HashSet<>(instance.cd.findByDialect(d.toString()));
    }

    /**
     * 把「词典代号」查询「名称」按照序列的方法排列，用于前端
     */
    public List<Pair<UString, DictCode>> getDictionaryMenu(Dialect dialect)
    {
        List<Pair<UString, DictCode>> ans = new ArrayList<>();
        for (var i : DictGroup.of(dialect).getDict().entrySet())
            ans.add(Pair.of(i.getValue().get(Language.TC), i.getKey()));
        return ans;
    }

    private static DictService instance;

    @PostConstruct
    public void init()
    {
        instance = this;
    }

    @Autowired
    private RefMapper ck;

    // 初始化词典

    public void initBook(DictCode d, int page)
    {
        if (ck.isDictionaryExist(d.toString()))
        {
            System.out.println("词典" + d + "已经初始化了");
        }
        else
        {
            ck.batchInsert(RefEntity.initBook(d, page));
            System.out.println("词典" + d + "加入成功");
        }
    }
}
