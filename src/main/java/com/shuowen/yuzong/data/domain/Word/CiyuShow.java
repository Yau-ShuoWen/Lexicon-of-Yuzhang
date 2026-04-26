package com.shuowen.yuzong.data.domain.Word;

import com.shuowen.yuzong.Linguistics.Scheme.RPinyins;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.RichTextUtil;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.data.domain.IPA.IPAData;
import com.shuowen.yuzong.Linguistics.Scheme.PinyinFormatter;
import com.shuowen.yuzong.data.domain.Reference.DictCode;
import com.shuowen.yuzong.data.domain.Reference.RefItem;
import com.shuowen.yuzong.service.impl.Reference.RefReadService;
import lombok.Data;

import java.util.*;

/**
 * 词语的展示类，传输之后用作内容展示界面使用
 */
@Data
public class CiyuShow
{
    private final UString ciyu;
    private final UString special;
    private final RPinyins mainPy;
    private final List<UString> variantPy;
    private final List<Pair<UString, Integer>> similar;
    private final List<UString> mean;
    private final LinkedHashSet<RefItem> ref = new LinkedHashSet<>();

    public static CiyuShow of(CiyuItem cy, final IPAData data)
    {
        return new CiyuShow(cy, data);
    }

    private CiyuShow(CiyuItem cy, final IPAData data)
    {
        var l = data.getLanguage();
        var d = data.getDialect();

        ciyu = cy.getCiyus().get(l);

        {
            String tmp = "用法和普通話基本相同。";
            if (cy.getSpecial() == 2) tmp = "";
            special = ScTcText.get("概覽：" + tmp, l);
        }

        {

        }
        similar = cy.getSimilar();
        mean = cy.getMean();

        mainPy = RPinyins.of(ListTool.mapping(cy.getMainPy(), i ->
                PinyinFormatter.handle(d.trustedCreatePinyin(i), d)
        ));

        variantPy = ListTool.mapping(cy.getVariantPy(),
                i -> RichTextUtil.format(i, data, false, Maybe.nothing(),true)
        );

        {
            ref.addAll(RefReadService.getRef(cy.getCiyus().getSc().toString(), DictCode.of("ncdict"), data));
            ref.addAll(RefReadService.getRef(cy.getCiyus().getTc().toString(), DictCode.of("ncdict"), data));
        }
    }
}
