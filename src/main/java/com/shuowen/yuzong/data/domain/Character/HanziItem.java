package com.shuowen.yuzong.data.domain.Character;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Linguistics.Scheme.SPinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.dataStructure.UChar;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcChar;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Twin;
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
    // 基本信息
    private final Integer id;
    private final UChar hanzi;
    private final ScTcChar hanzis;
    private final SPinyin mainPy;
    private final Integer special;

    // 连接表信息
    private final List<UChar> similar;
    private final List<Pair<UString, SPinyin>> variantPy;
    private final List<String> mdrInfo;

    // 原表复杂结构
    private final List<Pair<String, String>> ipa;
    private final List<Twin<UString>> note;
    private final List<Map<String, String>> refer;

    // 时间信息
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    protected HanziItem(HanziEntity ch, Language l)
    {
        id = ch.getId();
        hanzis = new ScTcChar(ch.getSc(), ch.getTc());
        hanzi = hanzis.get(l);
        mainPy = SPinyin.of(ch.getMainPy());
        special = ch.getSpecial();

        ObjectMapper om = new ObjectMapper();

        similar = ListTool.mapping(
                readJson(ch.getSimilar(), new TypeReference<List<ScTcChar>>() {}, om),
                i -> i.get(l)
        );

        // sc tc content 三个字段，sc tc创建Text之后选择语言，content直接获取
        variantPy = ListTool.mapping(
                readJson(ch.getVariantPy(), new TypeReference<List<Map<String, String>>>() {}, om),
                i -> Pair.of(new ScTcText(i.get("sc"), i.get("tc")).get(l), SPinyin.of(i.get("content")))
        );


        // 解析为 List<String>，并且筛选，条件：「mdrInfo里汉字」==「目前的汉字」
        mdrInfo = ListTool.filter(
                readJson(ch.getMdrInfo(), new TypeReference<>() {}, om),
                i -> i.contains(hanzi.toString())
        );

        // TODO ：这个字段从来没有被更新过用法
        ipa = ListTool.mapping(
                readJson(ch.getIpa(), new TypeReference<List<Map<String, String>>>() {}, om)
                , i -> Pair.of(i.get("tag"), i.get("content"))
        );

        // 解析为 Map< 简繁 , List<Map< 标签 , 内容 >>>>
        // 使用 .get(l)选取对应语言
        // 使用tag获得题目，content获取内容
        // 变成List<Pair<String, String>>

        note = ListTool.mapping(
                readJson(ch.getNote(), new TypeReference<List<Map<String, ScTcText>>>() {}, om),
                i -> Twin.of(i.get("tag").get(l), i.get("content").get(l))
        );

        // TODO ：这个字段从来没有被更新过用法
        refer = readJson(ch.getRefer(), new TypeReference<Map<String, List<Map<String, String>>>>() {}, om)
                .get(l.toString());

        createdAt = ch.getCreatedAt();
        updatedAt = ch.getUpdatedAt();
    }
}
