package com.shuowen.yuzong.data.domain.Word;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.Linguistics.Scheme.RPinyins;
import com.shuowen.yuzong.Linguistics.Scheme.SPinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.option.NoteTag;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.Linguistics.Scheme.PinyinFormatter;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Twin;
import com.shuowen.yuzong.data.model.Word.CiyuEntity;
import com.shuowen.yuzong.data.model.Word.CiyuSimilar;
import com.shuowen.yuzong.data.model.Word.CiyuTool;
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
    private final List<Pair<UString, Integer>> similar;
    private final List<Twin<UString>> note;
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

        //check
        mainPy = readJson(cy.getMainPy(), new TypeReference<>() {});

        similar = ListTool.mapping(
                readJson(cy.getSimilar(), new TypeReference<List<CiyuSimilar>>() {}),
                i -> Pair.of(new ScTcText(i.getSc(), i.getTc()).get(l), i.getType())
        );

        note = ListTool.mapping(
                readJson(cy.getNote(), new TypeReference<List<Pair<NoteTag, ScTcText>>>() {}),
                i -> Twin.of(i.getLeft().getName().get(l), i.getRight().get(l))
        );

        mean = ListTool.mapping(
                readJson(cy.getMean(), new TypeReference<List<ScTcText>>() {}),
                i -> i.get(l)
        );

        createdAt = cy.getCreatedAt();
        updatedAt = cy.getUpdatedAt();

        // 通过DAO创建简繁文本，然后一起插入
        matchItem.addAll(ciyus.getTwin().toList());
        for (var i : readJson(cy.getSimilar(), new TypeReference<List<CiyuSimilar>>() {}))
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
        return keyCache.computeIfAbsent(query,
                i -> Collections.max(matchItem,
                        Comparator.comparingDouble(s -> CiyuTool.weight(s.toString(), query))
                )
        );
    }

    public RPinyins getPinyin(Dialect d)
    {
        return RPinyins.of(ListTool.mapping(mainPy, i -> PinyinFormatter.handle(i, d)));
    }
}