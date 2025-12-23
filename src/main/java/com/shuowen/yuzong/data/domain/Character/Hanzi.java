package com.shuowen.yuzong.data.domain.Character;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.data.model.Character.CharEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;

import static com.shuowen.yuzong.Linguistics.Mandarin.TcSc.tagTrim;
import static com.shuowen.yuzong.Tool.format.JsonTool.*;

@Data
public class Hanzi
{
    protected Integer id;
    protected String hanzi;
    protected String hantz;
    protected String stdPy;
    protected Integer special;

    protected Map<String, List<String>> similar;
    protected List<Map<String, String>> mulPy;
    protected List<String> mdrInfo;
    protected List<Map<String, String>> ipaExp;
    protected Map<String, List<String>> mean;
    protected Map<String, List<Map<String, String>>> note;
    protected Map<String, List<Map<String, String>>> refer;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    @JsonIgnore
    protected static String TAG = "text";

    protected Hanzi(CharEntity ch, Language lang)
    {
        // 数据导入
        id = ch.getId();
        hanzi = ch.getHanzi();
        hantz = ch.getHantz();
        stdPy = ch.getStdPy();
        special = ch.getSpecial();

        ObjectMapper om = new ObjectMapper();
        similar = readJson(ch.getSimilar(), new TypeReference<>() {}, om);
        mulPy = readJson(ch.getMulPy(), new TypeReference<>() {}, om);
        mdrInfo = readJson(ch.getMdrInfo(), new TypeReference<>() {}, om);
        ipaExp = readJson(ch.getIpaExp(), new TypeReference<>() {}, om);

        mean = readJson(ch.getMean(), new TypeReference<>() {}, om);
        note = readJson(ch.getNote(), new TypeReference<>() {}, om);
        refer = readJson(ch.getRefer(), new TypeReference<>() {}, om);

        createdAt = ch.getCreatedAt();
        updatedAt = ch.getUpdatedAt();

        // 语言初始化：把SC TC标签简化
        for (var i : mulPy) tagTrim(i, lang, TAG);
        for (var i : ipaExp) tagTrim(i, lang, TAG);
        tagTrim(similar, lang, TAG);
        tagTrim(mean, lang, TAG);
        tagTrim(note, lang, TAG);
        tagTrim(refer, lang, TAG);
        // 语言初始化：过滤普通话读音内容
        mdrInfo = ListTool.filter(mdrInfo, i -> Objects.equals(
                i.split(" ")[0], (lang == Language.SC) ? hanzi : hantz));
    }

    public static Hanzi of(CharEntity ch, Language lang)
    {
        return new Hanzi(ch, lang);
    }

    public List<String> getSimilarData()
    {
        return new ArrayList<>(similar.get(TAG));
    }

    /**
     * 返回值 「读音标签 - 读音」列表
     */
    public List<Pair<String, String>> getMulPyData()
    {
        return ListTool.mapping(mulPy, i -> Pair.of(i.get(TAG), i.get("content")));
    }

    /**
     * 返回值 「字典代号 - 字典」列表，只返回代码是因为还需要去数据库反查
     */
    public List<Pair<String, String>> getIpaExpData()
    {
        return ListTool.mapping(ipaExp, i -> Pair.of(i.get("tag"), i.get("content")));
    }

    /**
     * 返回值 含义列表
     */
    public List<String> getMeanData()
    {
        return mean.get(TAG);
    }

    /**
     * 返回值 「描述标签 - 描述」 列表
     */
    public List<Pair<String, String>> getNoteData()
    {
        return ListTool.mapping(note.get(TAG), i -> Pair.of(i.get("tag"), i.get("content")));
    }
}
