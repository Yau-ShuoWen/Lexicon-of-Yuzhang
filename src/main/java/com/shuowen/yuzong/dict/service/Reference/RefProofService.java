package com.shuowen.yuzong.dict.service.Reference;

import com.shuowen.yuzong.dict.data.domain.Reference.DictCode;
import com.shuowen.yuzong.dict.data.domain.Reference.RefProof;
import com.shuowen.yuzong.dict.data.mapper.Reference.RefMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional (rollbackFor = {Exception.class})
public class RefProofService
{
    @Autowired
    private RefMapper ck;

    protected void editPage(DictCode dict, RefProof page)
    {
        ck.deleteInside(dict.getCode(), page.getFrontSort().toString(), page.getEndSort().toString());

        page.replace();

        var entity = page.transfer();
        ck.updateEdge(entity.getLeft().getLeft());
        ck.updateEdge(entity.getLeft().getRight());

        ck.batchInsert(entity.getRight());
    }
}