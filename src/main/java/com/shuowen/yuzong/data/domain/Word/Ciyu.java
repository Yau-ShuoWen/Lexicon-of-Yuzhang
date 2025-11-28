package com.shuowen.yuzong.data.domain.Word;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.data.model.Word.WordEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;

import static com.shuowen.yuzong.Tool.format.JsonTool.readJson;

@Data
public abstract class Ciyu<T extends UniPinyin, P extends PinyinStyle>
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

    protected Ciyu(WordEntity wd)
    {
        id = wd.getId();
        ObjectMapper om = new ObjectMapper();

        ciyu = new UString(wd.getCiyu());
        tszyu = new UString(wd.getTszyu());
        pinyin = readJson(wd.getPinyin(), new TypeReference<>() {}, om);
        mulPy = readJson(wd.getMulPy(), new TypeReference<>() {}, om);

        if (List.of(ciyu.length(), tszyu.length(), pinyin.size(), mulPy.size())
                .stream().distinct().count() != 1)
        {
            throw new IllegalArgumentException("信息数量不一样");
        }

        similar = readJson(wd.getSimilar(), new TypeReference<>() {}, om);
        mean = readJson(wd.getMean(), new TypeReference<>() {}, om);
        refer = readJson(wd.getRefer(), new TypeReference<>() {}, om);

        createdAt = wd.getCreatedAt();
        updatedAt = wd.getUpdatedAt();
    }

    //TODO：还没有根据样式格式化

    protected abstract T pinyinOf(String str);

    protected abstract String formatting(String s, P style);

    protected abstract String dict();

    public void changeLang(Language lang)
    {
        if (lang.isCH()) return;

        if (lang.isSC()) ciyu = UString.of("");
        else tszyu = UString.of("");

        String r = lang.reverse().toString();
        for (var i : mulPy) for (var j : i) j.remove(r);

        similar.remove(r);
        mean.remove(r);
    }

    protected void transfer(Language l)
    {

    }
}