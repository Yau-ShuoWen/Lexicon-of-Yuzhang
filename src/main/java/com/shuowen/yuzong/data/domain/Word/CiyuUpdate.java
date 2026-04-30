package com.shuowen.yuzong.data.domain.Word;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.Linguistics.Scheme.PinyinFormatter;
import com.shuowen.yuzong.Linguistics.Scheme.SPinyins;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ObjectTool;
import com.shuowen.yuzong.Tool.TextTool.TextPinyinIPA;
import com.shuowen.yuzong.Tool.dataStructure.Range;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.data.model.Word.CiyuEntity;
import com.shuowen.yuzong.data.model.Word.CiyuSimilar;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.shuowen.yuzong.Tool.format.JsonTool.readJson;
import static com.shuowen.yuzong.Tool.format.JsonTool.toJson;

@Data
@NoArgsConstructor
public class CiyuUpdate
{
    private Integer id;
    private ScTcText ciyu;
    private Integer special;

    private SPinyins mainPy;
    private List<ScTcText> variantPy;  //？？

    @Data
    @NoArgsConstructor
    public static class Similar
    {
        Integer id;      // 新增的内容id设置为0
        ScTcText text;
        Integer type;

        public Similar(CiyuSimilar sim)
        {
            id = sim.getId();
            text = new ScTcText(sim.getSc(), sim.getTc());
            type = sim.getType();
        }

        public CiyuSimilar transfer()
        {
            var ans = new CiyuSimilar();
            ans.setId(id);
            ans.setSc(text.getSc().toString());
            ans.setTc(text.getTc().toString());
            ans.setType(type);
            return ans;
        }
    }

    private List<Similar> similar = new ArrayList<>();

    private List<ScTcText> mean;

    // 数据库→后端→前端 ----------------------------------------------------

    public CiyuUpdate(Dialect d, CiyuEntity cy, List<CiyuSimilar> sim)
    {
        id = cy.getId();
        ciyu = new ScTcText(cy.getSc(), cy.getTc());
        special = cy.getSpecial();

        mainPy = SPinyins.of(
                ListTool.mapping(readJson(cy.getMainPy(), new TypeReference<List<String>>() {}),
                        i -> PinyinFormatter.toSPinyin(i, d)
                )
        );


        variantPy = readJson(cy.getVariantPy(), new TypeReference<>() {}); //？？

        similar = ListTool.mapping(sim, Similar::new);
        mean = ListTool.mapping(
                readJson(cy.getMean(), new TypeReference<List<ScTcText>>() {}),
                i -> i.map(str -> TextPinyinIPA.transferPinyin(str, d, true))
        );
    }

    // 前端→后端→数据库 ----------------------------------------------------

    public Pair<CiyuEntity, List<CiyuSimilar>> checkAndTransfer(Dialect d)
    {
        ObjectTool.asserts(ciyu.length() == mainPy.getPinyin().size(), "");

        var cy = new CiyuEntity();
        cy.setId(id);
        cy.setSc(ciyu.getSc().toString());
        cy.setTc(ciyu.getTc().toString());

        ObjectTool.asserts(Range.close(0, 3).contains(special), "");
        cy.setSpecial(special);

        cy.setMainPy(
                toJson(
                        ListTool.mapping(
                                mainPy.getPinyin(),
                                i -> PinyinFormatter.toDPinyin(d.checkAndCreatePinyin(i), d).toString(true)
                        )
                )
        );

        cy.setVariantPy(toJson(variantPy));

        var sim = ListTool.mapping(similar, Similar::transfer);

        cy.setMean(toJson(
                ListTool.mapping(mean,
                        i -> i.map(str -> TextPinyinIPA.transferPinyin(str, d, false))
                )
        ));

        return Pair.of(cy, sim);
    }
}
