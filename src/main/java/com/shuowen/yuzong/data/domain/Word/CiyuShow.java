package com.shuowen.yuzong.data.domain.Word;

import com.shuowen.yuzong.Linguistics.Scheme.RPinyins;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.util.text.RichTextUtil;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.util.text.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Scheme;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Twin;
import com.shuowen.yuzong.Linguistics.Scheme.PinyinFormatter;
import com.shuowen.yuzong.data.domain.IPA.PinyinMode;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinConfig;
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
    private final Integer special;
    private final RPinyins mainPy;
    private final List<Pair<UString, Integer>> similar;
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

        similar = List.of();//cy.getSimilar();

        Scheme scheme = data.getPinyinMode() == PinyinMode.INTRODUCE ? Scheme.INTRO : Scheme.DISPLAY;

        mainPy = RPinyins.of(ListTool.mapping(cy.getMainPy(), i ->
                PinyinFormatter.handle(d.trustedCreatePinyin(i), d, scheme)
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
