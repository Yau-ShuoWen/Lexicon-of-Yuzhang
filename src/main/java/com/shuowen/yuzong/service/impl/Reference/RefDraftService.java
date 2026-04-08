package com.shuowen.yuzong.service.impl.Reference;

import com.shuowen.yuzong.Tool.FractionIndex;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Twin;
import com.shuowen.yuzong.data.domain.Reference.DictCode;
import com.shuowen.yuzong.data.domain.Reference.RefDraft;
import com.shuowen.yuzong.data.mapper.Reference.RefMapper;
import com.shuowen.yuzong.data.model.Reference.RefEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional (rollbackFor = {Exception.class})
public class RefDraftService
{
    @Autowired
    private RefMapper ck;

    /**
     * 根据sort获得整页的内容
     */
    private List<RefEntity> getPageInfo(DictCode dict, FractionIndex sort)
    {
        return ck.getPageBySort(dict.getCode(), sort.toString());
    }

    /**
     * @param before 插入页在当前页面的位置
     *               <br>{@code true} - 前面，sort是开头的序号  {@code false} - 后面，sort是结尾的序号
     */
    protected RefDraft insertPage(DictCode dict, FractionIndex sort, boolean before)
    {
        var find = ck.findNearby(dict.getCode(), sort.toString(), before);

        // before == true  第1页结束标记（找到的） | <插入位置> | 第2页开始标记（发来的）
        // before == false 第2页结束标记（发来的） | <插入位置> | 第3页开始标记（找到的）
        // 所以如果false 就要翻转找到的和发来的位置
        var sorts = Twin.of(find.getTheSort(), sort);
        if (!before) sorts.swap();

        var list = RefEntity.initPage(dict, sorts);
        ck.batchInsert(list);
        return RefDraft.of(list);
    }

    /**
     * 覆盖整个内容
     */
    protected void overwritePage(DictCode dict, RefDraft page)
    {
        ck.deleteInside(dict.getCode(), page.getFrontSort().toString(), page.getEndSort().toString());

        var entity = page.transfer();
        ck.updateEdge(entity.getLeft().getLeft());
        ck.updateEdge(entity.getLeft().getRight());
        ck.batchInsert(entity.getRight());
    }
}
