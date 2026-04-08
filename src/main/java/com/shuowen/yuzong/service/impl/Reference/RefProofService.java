package com.shuowen.yuzong.service.impl.Reference;

import com.shuowen.yuzong.Tool.DataVersionCtrl.ListCompareUtil;
import com.shuowen.yuzong.Tool.FractionIndex;
import com.shuowen.yuzong.data.domain.Reference.DictCode;
import com.shuowen.yuzong.data.domain.Reference.RefProof;
import com.shuowen.yuzong.data.mapper.Reference.RefMapper;
import com.shuowen.yuzong.data.model.Reference.RefEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional (rollbackFor = {Exception.class})
public class RefProofService
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

    protected void editPage(DictCode dict, RefProof page)
    {
        ck.deleteInside(dict.getCode(), page.getFrontSort().toString(), page.getEndSort().toString());

        var entity = page.transfer();
        ck.updateEdge(entity.getLeft().getLeft());
        ck.updateEdge(entity.getLeft().getRight());

        ck.batchInsert(entity.getRight());
    }
}
//        var oldData = getPageInfo(dict, page.getFrontSort());
//        var newData = page.transfer();

//        List<RefEntity> addData = new ArrayList<>();  // 新增的，整个DAO
//        List<RefEntity> modData = new ArrayList<>();  // 修改的，整个DAO
//        List<Integer> delData = new ArrayList<>();    // 删除的，id就够了
//
//        System.out.println(oldData);
//        System.out.println();
//        System.out.println(newData);
//        System.out.println();
//
//        System.out.println(ListCompareUtil.compare(oldData, newData));
//        System.out.println();
//
//        for (var i : ListCompareUtil.compare(oldData, newData))
//        {
//            switch (i.getChangeType())
//            {
//                case ADDED -> addData.add(i.getNewItem());
//                case MODIFIED -> modData.add(i.getNewItem()); // 不设置id，差异检测决定了被分配到这的肯定是id确定的
//                case DELETED -> delData.add(i.getOldItem().getId());
//            }
//        }
//
//        if (!addData.isEmpty()) ck.batchInsert(addData);
//        if (!delData.isEmpty()) ck.batchDelete(dict.getCode(), delData);
//        if (!modData.isEmpty()) for (var i:modData) ck.update(i);
//        System.out.println(getPageInfo(dict, page.getFrontSort()));
