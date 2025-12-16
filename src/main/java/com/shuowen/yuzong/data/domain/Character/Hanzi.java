package com.shuowen.yuzong.data.domain.Character;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.data.dto.Character.HanziOutline;
import com.shuowen.yuzong.data.model.Character.CharEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;

import static com.shuowen.yuzong.Tool.JavaUtilExtend.MapTool.renameKey;
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

    private boolean selectLang = false;

    protected Hanzi(CharEntity ch)
    {
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
    }

    public static Hanzi of(CharEntity ch)
    {
        return new Hanzi(ch);
    }

    // 转化为展示类 --------------------------------------------------

    /**
     * 对内容区分简繁体的字段确定个版本（避免传输消耗）
     */
    public void changeLang(Language lang)
    {
        String l = lang.toString();
        String r = lang.reverse().toString();

        for (var i : mulPy) erasure(i, l, r);
        for (var i : ipaExp) erasure(i, l, r);

        erasure(similar, l, r);
        erasure(mean, l, r);
        erasure(note, l, r);
        erasure(refer, l, r);

        selectLang = true;
    }

    protected void checkLang()
    {
        if (!selectLang) throw new RuntimeException("流程缺失，应该先调用 changeLang");
    }


    /**
     * 标签「擦除」，当确定简体繁体之后，和简繁体有关的标签区分没有用处了，所以把标签简化，并且删除不需要的语言
     */
    private <T extends Map> T erasure(T map, String l, String r)
    {
        map.remove(r);
        renameKey(map, l, "text");
        return map;
    }

    public List<String> getSimilarData()
    {
        checkLang();
        return new ArrayList<>(similar.get("text"));
    }

    /**
     * 返回值 「读音标签 - 读音」列表
     */
    public List<Pair<String, String>> getMulPyData()
    {
        checkLang();
        List<Pair<String, String>> ans = new ArrayList<>();
        for (var i : mulPy) ans.add(Pair.of(i.get("text"), i.get("content")));
        return ans;
    }

    /**
     * 返回值 「字典代号 - 字典」列表，只返回代码是因为还需要去数据库反查
     */
    public List<Pair<String, String>> getIpaExpData()
    {
        checkLang();
        List<Pair<String, String>> ans = new ArrayList<>();
        for (var i : ipaExp) ans.add(Pair.of(i.get("tag"), i.get("content")));
        return ans;
    }

    /**
     * 返回值 含义列表
     */
    public List<String> getMeanData()
    {
        checkLang();
        return mean.get("text");
    }

    /***
     * 返回值 「描述标签 - 描述」 列表
     */
    public List<Pair<String, String>> getNoteData()
    {
        checkLang();
        List<Pair<String, String>> ans = new ArrayList<>();
        for (var i : note.get("text")) ans.add(Pair.of(i.get("tag"), i.get("content")));
        return ans;
    }

    // 转化为筛选类 --------------------------------------------------

    public HanziOutline transfer()
    {
        HanziOutline out = new HanziOutline();

        out.setId(id);
        out.setHanzi(hanzi);
        out.setHantz(hantz);
        out.setStdPy(stdPy);

        return out;
    }
}
