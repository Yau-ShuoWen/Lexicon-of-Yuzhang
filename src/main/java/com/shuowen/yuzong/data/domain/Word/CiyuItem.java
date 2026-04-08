package com.shuowen.yuzong.data.domain.Word;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Linguistics.Scheme.RPinyin;
import com.shuowen.yuzong.Linguistics.Scheme.SPinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.WeightSort;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.Linguistics.Scheme.PinyinFormatter;
import com.shuowen.yuzong.data.model.Word.CiyuEntity;
import com.shuowen.yuzong.data.model.Word.CiyuSimilar;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;

import static com.shuowen.yuzong.Tool.format.JsonTool.readJson;

/**
 * 词语的词条类，从数据库取出来之后直接转换而成
 */
@Data
public class CiyuItem
{
    // 基本数据
    private final Integer id;
    private final UString ciyu;
    private final ScTcText ciyus;
    private final Integer special;

    // 结构组数据
    private final List<SPinyin> mainPy;
    private final List<UString> variantPy;
    private final List<Pair<UString, Integer>> similar;
    private final List<UString> mean;

    // 时间数据
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    // 匹配數據
    private final List<UString> matchItem = new ArrayList<>();
    private final Map<String, UString> keyCache = new HashMap<>();

    private CiyuItem(CiyuEntity cy, Language l)
    {
        id = cy.getId();
        ciyus = new ScTcText(cy.getSc(), cy.getTc());
        ciyu = ciyus.get(l);
        special = cy.getSpecial();

        ObjectMapper om = new ObjectMapper();

        //check
        mainPy = readJson(cy.getMainPy(), new TypeReference<>() {}, om);
        variantPy = readJson(cy.getVariantPy(), new TypeReference<>() {}, om);

        similar = ListTool.mapping(
                readJson(cy.getSimilar(), new TypeReference<List<CiyuSimilar>>() {}, om),
                i -> Pair.of(new ScTcText(i.getSc(), i.getTc()).get(l), i.getType())
        );

        mean = ListTool.mapping(
                readJson(cy.getMean(), new TypeReference<List<ScTcText>>() {}, om),
                i -> i.get(l)
        );

        createdAt = cy.getCreatedAt();
        updatedAt = cy.getUpdatedAt();

        // 通过DAO创建简繁文本，然后一起插入
        matchItem.addAll(ciyus.getTwin().toList());
        for (var i : readJson(cy.getSimilar(), new TypeReference<List<CiyuSimilar>>() {}, om))
        {
            matchItem.addAll(new ScTcText(i.getSc(), i.getTc())
                    .getTwin().toList());
        }
    }

    public static CiyuItem of(CiyuEntity cy, Language lang)
    {
        return new CiyuItem(cy, lang);
    }

    public static List<CiyuItem> listOf(List<CiyuEntity> cy, Language l)
    {
        return ListTool.mapping(cy, i -> CiyuItem.of(i, l));
    }

    // 获得和查询内容最接近的一个作为排序内容
    public UString getSortKey(String query)
    {
        if (keyCache.containsKey(query)) return keyCache.get(query);

        // 主词条是1.0，繁体简体各一个，模糊识别0.0，2*similar个
        List<Double> priority = ListTool.nCopies(
                Pair.of(2, 1.0),
                Pair.of(similar.size() * 2, 0.0)
        );

        WeightSort.sort(matchItem, priority, UString::toString, query, null);

        keyCache.put(query, matchItem.get(0));
        return matchItem.get(0);
    }

    public List<RPinyin> getPinyin(Dialect d)
    {
        return ListTool.mapping(mainPy, i -> PinyinFormatter.handle(i, d));
    }
}