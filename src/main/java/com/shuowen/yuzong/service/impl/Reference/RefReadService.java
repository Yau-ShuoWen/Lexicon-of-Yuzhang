package com.shuowen.yuzong.service.impl.Reference;

import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.UniqueList;
import com.shuowen.yuzong.Tool.RichTextUtil;
import com.shuowen.yuzong.data.domain.IPA.IPAData;
import com.shuowen.yuzong.data.domain.Reference.DictCode;
import com.shuowen.yuzong.data.domain.Reference.RefItem;
import com.shuowen.yuzong.data.mapper.Reference.RefMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional (rollbackFor = {Exception.class})
public class RefReadService
{
    @Autowired
    private RefMapper ck;

    public List<RefItem> getAllRef(String query, DictCode dict, final IPAData data)
    {
        return ListTool.mapping(ck.findByQuery(dict.getCode(), query), i -> new RefItem(i, data));
    }

    private static RefReadService instance;

    @PostConstruct
    public void init()
    {
        instance = this;
    }

    public static List<RefItem> getRef(String query, DictCode dict, final IPAData data)
    {
        return instance.getAllRef(query, dict, data);
    }
}
