package com.shuowen.yuzong.dao.domain.Character.dialect;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.Linguistics.Scheme.NamPinyin;
import com.shuowen.yuzong.dao.domain.Character.Hanzi;
import com.shuowen.yuzong.dao.model.Character.CharEntity;

import java.util.*;
import java.util.function.Function;

public class NamHanzi extends Hanzi<NamPinyin, NamStyle>
{
    /**
     * @param statue 1 全部拼音 2 部分拼音 3.全部ipa
     * @param ipaSE 國際音標的「搜索平臺(Search Engine)」
     * */
    protected NamHanzi(CharEntity ch, NamStyle style, int statue,
                    Function<Set<NamPinyin>, Map<NamPinyin, Map<String, String>>> ipaSE)
    {
        super(ch);
        if (statue < 1 || statue > 3) statue = 1;
        switch (statue)
        {
            case 1 -> initPinyin(style, true);
            case 2 ->
            {
                initPinyin(style, false);
                initIPA(ipaSE, false);
            }
            case 3 -> initIPA(ipaSE, true);
        }
    }

    /**
     * 全部使用拼音版本
     * */
    public static NamHanzi of(CharEntity ch, NamStyle style)
    {
        return new NamHanzi(ch, style,1,null);
    }

    /**
     * 除了和字典相关数据以外其他使用
     * */
    public static NamHanzi of(CharEntity ch, NamStyle style,
                              Function<Set<NamPinyin>, Map<NamPinyin, Map<String, String>>> ipaSE)
    {
        return new NamHanzi(ch, style, 2, ipaSE);
    }

    /**
     * 全部使用国际音标版本
     * */
    public static NamHanzi of(CharEntity ch, Function<Set<NamPinyin>, Map<NamPinyin, Map<String, String>>> ipaSE)
    {
        return new NamHanzi(ch,null,3,ipaSE);
    }

    /**
     * 所有拼音按照普通的内容初始化
     * */
    protected void initPinyin(NamStyle style, Boolean all)
    {
        stdPy = NamPinyin.formatting(stdPy, style);
        for (var i : mulPy.values())
            i.put("content", NamPinyin.formatting(i.get("content"), style));

        if (all)
        {
            for (var i : ipaExp)
                i.put("content", NamPinyin.formatting(i.get("content"), style));
        }
    }


    /**
     * 批量 IPA 初始化方法
     */
    protected void initIPA(Function<Set<NamPinyin>, Map<NamPinyin, Map<String, String>>> ipaSE, Boolean all)
    {
        // 收集所有需要查询的拼音
        Set<NamPinyin> allPinyin = new HashSet<>();
        if (all)
        {
            allPinyin.add(NamPinyin.of(stdPy));
            for (var i : mulPy.values())
                allPinyin.add(NamPinyin.of(i.get("content")));
        }
        for (var i : this.getIpaExp())
            allPinyin.add(NamPinyin.of(i.get("content")));


        // 批量查询 IPA
        Map<NamPinyin, Map<String, String>> ipaMap = ipaSE.apply(allPinyin);

        // 更新
        NamPinyin stdPinyin = NamPinyin.of(stdPy);
        Map<String, String> stdIpa = ipaMap.get(stdPinyin);


        if (all)
        {
            stdPy = stdIpa.get("ncDict");
            for (var i : mulPy.values())
                i.put("content", ipaMap.get(NamPinyin.of(i.get("content"))).get("ncdict"));
        }
        for (var i : ipaExp)
            i.put("content", ipaMap.get(NamPinyin.of(i.get("content"))).get(i.get("tag")));
    }
}
