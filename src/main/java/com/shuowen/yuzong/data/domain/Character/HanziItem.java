package com.shuowen.yuzong.data.domain.Character;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.data.model.Character.HanziEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;

import static com.shuowen.yuzong.Linguistics.Mandarin.TcSc.tagTrim;
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

    private final Map<String, List<String>> similar;
    private final List<Map<String, String>> variantPy;
    private final List<String> mdrInfo;
    private final List<Map<String, String>> ipa;
    private final Map<String, List<String>> mean;
    private final Map<String, List<Map<String, String>>> note;
    private final Map<String, List<Map<String, String>>> refer;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @JsonIgnore
    protected static String TEXT = "text";

    protected HanziItem(HanziEntity ch, Language lang)
    {
        // 数据导入
        id = ch.getId();
        hanzi = lang.isSimplified() ? ch.getSc() : ch.getTc();
        mainPy = ch.getMainPy();
        special = ch.getSpecial();

        ObjectMapper om = new ObjectMapper();
        similar = readJson(ch.getSimilar(), new TypeReference<>() {}, om);
        variantPy = readJson(ch.getVariantPy(), new TypeReference<>() {}, om);
        mdrInfo = readJson(ch.getMdrInfo(), new TypeReference<>() {}, om);
        ipa = readJson(ch.getIpa(), new TypeReference<>() {}, om);

        mean = readJson(ch.getMean(), new TypeReference<>() {}, om);
        note = readJson(ch.getNote(), new TypeReference<>() {}, om);
        refer = readJson(ch.getRefer(), new TypeReference<>() {}, om);

        createdAt = ch.getCreatedAt();
        updatedAt = ch.getUpdatedAt();

        // 语言初始化：把SC TC标签简化
        for (var i : variantPy) tagTrim(i, lang, TEXT);
        for (var i : ipa) tagTrim(i, lang, TEXT);
        tagTrim(similar, lang, TEXT);
        tagTrim(mean, lang, TEXT);
        tagTrim(note, lang, TEXT);
        tagTrim(refer, lang, TEXT);

        // 语言初始化：过滤普通话读音内容
        ListTool.filter(mdrInfo, i -> Objects.equals(i.split(" ")[0], hanzi));
    }

    public static HanziItem of(HanziEntity ch, Language lang)
    {
        return new HanziItem(ch, lang);
    }

    public List<String> getSimilarData()
    {
        return new ArrayList<>(similar.get(TEXT));
    }

    /**
     * 返回值 「读音标签 - 读音」列表
     */
    public List<Pair<String, String>> getMulPyData()
    {
        return ListTool.mapping(variantPy, i -> Pair.of(i.get(TEXT), i.get("content")));
    }

    /**
     * 返回值 「字典代号 - 字典」列表，只返回代码是因为还需要去数据库反查
     */
    public List<Pair<String, String>> getIpaExpData()
    {
        return ListTool.mapping(ipa, i -> Pair.of(i.get("tag"), i.get("content")));
    }

    /**
     * 返回值 含义列表
     */
    public List<String> getMeanData()
    {
        return mean.get(TEXT);
    }

    /**
     * 返回值 「描述标签 - 描述」 列表
     */
    public List<Pair<String, String>> getNoteData()
    {
        return ListTool.mapping(note.get(TEXT), i -> Pair.of(i.get("tag"), i.get("content")));
    }
}
