package com.shuowen.yuzong.data.domain.Word;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ObjectTool;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.data.model.Word.WordEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;

import static com.shuowen.yuzong.Linguistics.Mandarin.TcSc.tagTrim;
import static com.shuowen.yuzong.Tool.format.JsonTool.readJson;

@Data
public class Ciyu
{
    protected Integer id;

    protected UString ciyu;
    protected UString tszyu;
    protected List<String> pinyin;
    protected List<List<Map<String, String>>> mulPy;

    protected Map<String, List<String>> similar;
    protected Map<String, List<String>> mean;
    protected Map<String, List<Map<String, String>>> refer;
    //TODO:example不应该在这个地方出现

    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    @JsonIgnore
    protected static String TAG = "text";

    protected Ciyu(WordEntity wd, Language lang)
    {
        id = wd.getId();
        ObjectMapper om = new ObjectMapper();

        ciyu = new UString(wd.getCiyu());
        tszyu = new UString(wd.getTszyu());
        pinyin = readJson(wd.getPinyin(), new TypeReference<>() {}, om);
        mulPy = readJson(wd.getMulPy(), new TypeReference<>() {}, om);

        similar = readJson(wd.getSimilar(), new TypeReference<>() {}, om);
        mean = readJson(wd.getMean(), new TypeReference<>() {}, om);
        refer = readJson(wd.getRefer(), new TypeReference<>() {}, om);

        createdAt = wd.getCreatedAt();
        updatedAt = wd.getUpdatedAt();

        tagTrim(similar, lang, TAG);
        for (var i : mulPy) for (var j : i) tagTrim(j, lang, TAG);
        tagTrim(mean, lang, TAG);
        tagTrim(refer, lang, TAG);
    }

    public static List<Ciyu> listOf(List<WordEntity> wd, Language lang)
    {
        List<Ciyu> list = new ArrayList<>();
        for (WordEntity we : wd) list.add(new Ciyu(we, lang));
        return list;
    }

    protected void checkLen()
    {
        if (!ObjectTool.allEqual(ciyu.length(), tszyu.length(), pinyin.size()/*, mulPy.size()*/))
            throw new IllegalArgumentException("词条异常：信息数量不一样");
    }

    public List<String> getSimilarData()
    {
        return similar.get(TAG);
    }

    public List<List<Pair<String, String>>> getMulPyData()
    {
        List<List<Pair<String, String>>> ans = new ArrayList<>();
        for (var i : mulPy)
        {
            List<Pair<String, String>> tmp = new ArrayList<>();
            for (var j : i) tmp.add(Pair.of(j.get(TAG), j.get("pinyin")));
            ans.add(tmp);
        }
        return ans;
    }

    public List<String> getMeanData()
    {
        return mean.get(TAG);
    }

    public List<Map<String, String>> getReferData()
    {
        return refer.get(TAG);
    }
}