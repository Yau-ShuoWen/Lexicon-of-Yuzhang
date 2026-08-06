package com.shuowen.yuzong.dict.data.domain.Word;

import com.shuowen.yuzong.dict.data.domain.Pinyin.PinyinConfig;
import com.shuowen.yuzong.dict.data.domain.Reference.RefItem;
import com.shuowen.yuzong.dict.service.Reference.RefReadService;
import com.shuowen.yuzong.linguistics.util.RPinyins;
import com.shuowen.yuzong.util.ext.list.ListTool;
import com.shuowen.yuzong.util.text.RichTextUtil;
import com.shuowen.yuzong.util.text.UString;
import com.shuowen.yuzong.util.tuple.Maybe;
import com.shuowen.yuzong.util.tuple.Twin;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * 词语的展示类，传输之后用作内容展示界面使用
 */
@Data
public class CiyuShow
{
    private final UString ciyu;
    private final Integer special;
    private final RPinyins mainPy;
    private final List<Twin<UString>> note;
    private final List<UString> mean;
    private final LinkedHashSet<RefItem> ref = new LinkedHashSet<>();

    public static CiyuShow of(CiyuItem cy, PinyinConfig data)
    {
        return new CiyuShow(cy, data);
    }

    private CiyuShow(CiyuItem cy, PinyinConfig data)
    {
        var l = data.getLanguage();
        var d = data.getDialect();

        ciyu = cy.getCiyus().get(l);
        special = cy.getSpecial();

        mainPy = RPinyins.of(ListTool.mapping(cy.getMainPy(), i ->
                d.trustedCreatePinyin(i).toRPinyin()
        ));

        note = ListTool.mapping(cy.getNote(),
                i -> Twin.of(i.getLeft(),
                        RichTextUtil.format(i.getRight(), data, false, Maybe.nothing(), true))
        );

        mean = ListTool.mapping(cy.getMean(),
                i -> RichTextUtil.format(i, data, false, Maybe.nothing(), true)
        );

        ref.addAll(RefReadService.getRef(cy.getCiyus(), data));
    }
}
