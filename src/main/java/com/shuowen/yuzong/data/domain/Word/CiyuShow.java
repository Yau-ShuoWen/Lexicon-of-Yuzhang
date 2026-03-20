package com.shuowen.yuzong.data.domain.Word;

import com.shuowen.yuzong.Linguistics.Scheme.RPinyin;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.data.domain.IPA.IPAData;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinFormatter;
import lombok.Data;

import java.util.*;
import java.util.function.Function;

/**
 * 词语的展示类，传输之后用作内容展示界面使用
 */
@Data
public class CiyuShow
{
    private final UString ciyu;
    private final Integer special;
    private final List<RPinyin> mainPy;
    private final List<List<RPinyin>> variantPy;
    private final List<Pair<UString, Integer>> similar;
    private final List<UString> mean;

    public static CiyuShow of(CiyuItem cy, final IPAData data)
    {
        return new CiyuShow(cy, data);
    }

    private CiyuShow(CiyuItem cy, final IPAData data)
    {
        Dialect d = data.getDialect();

        ciyu = cy.getCiyu();
        special = cy.getSpecial();
        similar = cy.getSimilar();
        mean = cy.getMean();

        @Data
        class tmpInfo
        {
            List<UniPinyin<?>> mainPy;
            List<List<UniPinyin<?>>> variantPy;
        }

        tmpInfo tmp = new tmpInfo();
        tmp.mainPy = ListTool.mapping(cy.getMainPy(), d::trustedCreatePinyin);
        tmp.variantPy = ListTool.mapping(cy.getVariantPy(), i -> ListTool.mapping(i, d::trustedCreatePinyin));

        Function<UniPinyin<?>, RPinyin> format = p -> PinyinFormatter.handle(p, d);
        
        switch (data.getPinyinOption().getPhonogram())
        {
            case AllPinyin, PinyinIPA ->
            {
                mainPy = ListTool.mapping(tmp.mainPy, format);
                variantPy = ListTool.mapping(tmp.variantPy, i -> ListTool.mapping(i, format));
            }
            default -> throw new RuntimeException();
        }
    }
}
