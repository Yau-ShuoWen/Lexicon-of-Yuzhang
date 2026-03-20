package com.shuowen.yuzong.data.domain.Word;

import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.data.model.Word.CiyuEntity;
import com.shuowen.yuzong.data.model.Word.CiyuSimilar;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CiyuUpdate
{
    Integer id;
    ScTcText ciyu;
    Integer special;

    // 拼音

    List<CiyuSimilar> similar = new ArrayList<>();

    // 数据库→后端→前端 ----------------------------------------------------

    public CiyuUpdate(CiyuEntity cy, List<CiyuSimilar> sim)
    {
        id = cy.getId();
        ciyu = new ScTcText(cy.getSc(), cy.getTc());
        special = cy.getSpecial();

        similar.addAll(sim);
    }

    // 前端→后端→数据库 ----------------------------------------------------

    public CiyuUpdate()
    {
    }

    public void check()
    {
    }

    public Pair<CiyuEntity, List<CiyuSimilar>> transfer()
    {
        return Pair.of(null,null);
    }
}
