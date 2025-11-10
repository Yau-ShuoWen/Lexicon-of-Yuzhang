package com.shuowen.yuzong.service.impl.Refer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Tool.DataVersionCtrl.ChangeResult;
import com.shuowen.yuzong.Tool.DataVersionCtrl.ListCompareUtil;
import com.shuowen.yuzong.Tool.FractionalIndexing;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.dao.domain.Refer.Citiao;
import com.shuowen.yuzong.dao.dto.Refer.CitiaoEdit;
import com.shuowen.yuzong.dao.mapper.Refer.DictMapper;
import com.shuowen.yuzong.dao.mapper.Refer.ReferMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.shuowen.yuzong.Tool.format.JsonTool.readJson;

@Service
public class ReferServiceImpl
{
    @Autowired
    ReferMapper m;

    @Autowired
    DictMapper n;

    public List<Pair<String, String>> getDictionaries()
    {
        List<Pair<String, String>> ans = new ArrayList<>();
        for (var i : n.getAll())
        {
            Map<String, String> tmp = readJson(i.getName(), new TypeReference<>() {}, new ObjectMapper());
            ans.add(Pair.of(tmp.get("tc"), i.getCode()));
        }
        return ans;
    }

    public List<Citiao> scopeSearch(String keyword, String dict)
    {
        if (keyword == null || dict == null || keyword.isEmpty() || dict.isEmpty())
            throw new RuntimeException();

        if (keyword.matches("^[Pp]\\d{1,4}$"))
        {
            return Citiao.listOf(m.pageSearch(Integer.parseInt(keyword.substring(1)),dict));
        }
        else
        {
            return Citiao.listOf(m.fuzzySearch(keyword, dict));
        }
    }

    public List<Citiao> getContext(Integer id, String dict)
    {
        return Citiao.listOf(m.getContext(id, dict));
    }

    @Transactional
    public void batchUpdate(Pair<List<CitiaoEdit>, List<CitiaoEdit>> v)
    {
        for (var i : ListCompareUtil.compare(Citiao.listBy(v.getLeft()), Citiao.listBy(v.getRight())))
        {
            switch (i.getChangeType())
            {
                case ADDED -> add(i);
                case MODIFIED -> update(i);
                case DELETED -> delete(i);
            } ;
        }
    }

    /**
     * 因为FractionalIndexing算法如果不给边界就会随意来，所以不可以头插尾插，如果要的话就修改编辑的范围使得有上下文
     */
    private void add(ChangeResult<Citiao> i)
    {
        if (i.getPreviousItem() == null || i.getNextItem() == null)
            throw new RuntimeException("边界");
        String sort = FractionalIndexing.generateFractionalIndexBetween(
                i.getPreviousItem().getSort(), i.getNextItem().getSort());
        i.getNewItem().setSort(sort);
        m.insert(i.getNewItem().transfer());
    }

    private void update(ChangeResult<Citiao> i)
    {
        m.update(i.getNewItem().transfer(), i.getChangedFields());
    }

    private void delete(ChangeResult<Citiao> i)
    {
        m.delete(i.getOldItem().getId());
    }
}
