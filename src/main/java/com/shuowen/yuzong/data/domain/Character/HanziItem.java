package com.shuowen.yuzong.data.domain.Character;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.data.model.Character.HanziEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;

import static com.shuowen.yuzong.Tool.format.JsonTool.*;

/**
 * 格式化的汉字主体
 */
@Data
public class HanziItem
{
    private final Integer id;
    private final String hanzi;
    private final String mainPy;
    private final Integer special;

    private final List<String> similar;
    private final List<Pair<String, String>> variantPy;
    private final List<String> mdrInfo;
    private final List<Pair<String, String>> ipa;
    private final List<String> mean;
    private final List<Pair<String, String>> note;
    private final List<Map<String, String>> refer;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    protected HanziItem(HanziEntity ch, Language lang)
    {
        id = ch.getId();
        hanzi = lang.isSimplified() ? ch.getSc() : ch.getTc();
        mainPy = ch.getMainPy();
        special = ch.getSpecial();

        ObjectMapper om = new ObjectMapper();
        String l = lang.toString();

        // 解析为 Map< 简繁标签 , List<String>>
        // 使用 .get(l)选取对应语言
        // 得到 List<String>
        similar = readJson(ch.getSimilar(), new TypeReference<Map<String, List<String>>>() {}, om)
                .get(l);

        // 解析为 List< 标签 ,Map<String,String>> 标签：简、繁标签，拼音内容
        // 使用 .get(l)选取对应语言，使用content获取拼音内容
        // 得到 List<Pair< 标签, 拼音 >>
        variantPy = ListTool.mapping(
                readJson(ch.getVariantPy(), new TypeReference<List<Map<String, String>>>() {}, om)
                , i -> Pair.of(i.get(l), i.get("content")));

        // 解析为 List<String>，并且筛选
        // 筛选条件：「mdrInfo里汉字」==「目前的汉字」，为了筛选简繁体
        mdrInfo = ListTool.filter(
                readJson(ch.getMdrInfo(), new TypeReference<>() {}, om),
                i -> Objects.equals(i.split(" ")[0], hanzi)
        );

        // TODO ：这个字段从来没有被更新过用法
        ipa = ListTool.mapping(
                readJson(ch.getIpa(), new TypeReference<List<Map<String, String>>>() {}, om)
                , i -> Pair.of(i.get("tag"), i.get("content")));

        // 解析为 Map< 简繁 , List<String>>
        // 使用 .get(l)选取对应语言
        mean = readJson(ch.getMean(), new TypeReference<Map<String, List<String>>>() {}, om)
                .get(l);

        // 解析为 Map< 简繁 , List<Map< 标签 , 内容 >>>>
        // 使用 .get(l)选取对应语言
        // 使用tag获得题目，content获取内容
        // 变成List<Pair<String, String>>
        note = ListTool.mapping(
                readJson(ch.getNote(), new TypeReference<Map<String, List<Map<String, String>>>>() {}, om)
                        .get(l),
                i -> Pair.of(i.get("tag"), i.get("content"))
        );

        // TODO ：这个字段从来没有被更新过用法
        refer = readJson(ch.getRefer(), new TypeReference<Map<String, List<Map<String, String>>>>() {}, om)
                .get(l);

        createdAt = ch.getCreatedAt();
        updatedAt = ch.getUpdatedAt();
    }

    public static HanziItem of(HanziEntity ch, Language lang)
    {
        return new HanziItem(ch, lang);
    }
}
