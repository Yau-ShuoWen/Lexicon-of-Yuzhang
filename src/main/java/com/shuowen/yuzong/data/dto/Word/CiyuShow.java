package com.shuowen.yuzong.data.dto.Word;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.WeightSort;
import com.shuowen.yuzong.Tool.dataStructure.functions.TriFunction;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.data.domain.IPA.PinyinOption;
import com.shuowen.yuzong.data.domain.Word.Ciyu;
import com.shuowen.yuzong.data.domain.Word.CiyuEntry;
import lombok.Data;

import java.util.*;

@Data
public class CiyuShow
{
    protected String ciyu;
    protected String language;
    protected List<String> pinyin;
    protected List<List<Pair<String, String>>> mulPy;
    protected List<String> similar;
    protected List<String> mean;
    protected List<Map<String, String>> refer;

    protected final String query;

    protected CiyuShow(Ciyu c, Language language, String query)
    {
        ciyu = ((language == Language.SC) ? c.getCiyu() : c.getTszyu()).toString();
        this.language = language.toString();

        pinyin = c.getPinyin();
        mulPy = c.getMulPyData();
        similar = c.getSimilarData();
        mean = c.getMeanData();
        refer = c.getReferData();
        this.query = query;
    }

    public static List<CiyuShow> listOf(CiyuEntry cy, String query)
    {
        List<CiyuShow> ans = new ArrayList<>();
        for (var i : cy.getList()) ans.add(new CiyuShow(i, cy.getLanguage(), query));
        return ans;
    }

    @JsonIgnore
    public Double getPrioriy()
    {
        return (getKey().equals(ciyu)) ? 1.0 : 0.0;
    }

    public static List<Double> getPrioriyList(List<CiyuShow> list)
    {
        List<Double> l = new ArrayList<>();
        for (var i : list) l.add(i.getPrioriy());
        return l;
    }

    // 获得和查询内容最接近的一个关键词
    @JsonIgnore
    public String getKey()
    {
        List<String> list = new ArrayList<>();
        list.add(ciyu);
        list.addAll(similar);

        // 标准的都是 1.0 ，剩下的模糊识别的都是 0.0
        List<Double> priority = new ArrayList<>();
        priority.add(1.0);
        priority.addAll(Collections.nCopies(similar.size(), 0.0));

        WeightSort.sort(list, priority, query, null);

        return list.get(0);
    }

    public <T extends UniPinyin<U>, U extends PinyinStyle>
    void init(U style, PinyinOption op, Dialect d, Map<String, String> dictInfo,
              TriFunction<Set<T>, PinyinOption, Dialect, Map<T, Map<String, String>>> ipaSE
    )
    {

    }
}
