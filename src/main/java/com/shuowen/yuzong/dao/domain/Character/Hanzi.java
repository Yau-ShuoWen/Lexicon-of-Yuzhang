package com.shuowen.yuzong.dao.domain.Character;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Tool.dataStructure.Language;
import com.shuowen.yuzong.Tool.dataStructure.Pair;
import com.shuowen.yuzong.Tool.dataStructure.Triple;
import com.shuowen.yuzong.dao.model.Character.CharEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;

import static com.shuowen.yuzong.Tool.MapTool.renameKey;
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
    protected Map<String, List<String>> pyExplain;
    protected List<Map<String, String>> ipaExp;
    protected Map<String, List<String>> mean;
    protected Map<String, List<String>> note;
    protected Map<String, List<Map<String, String>>> refer;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    public static String emptyScTc = "{\"sc\": [], \"tc\": []}";

    /**
     * 默认构造函数
     */
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
        pyExplain = readJson(ch.getPyExplain(), new TypeReference<>() {}, om);
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

    //  和转换为高级传输格式有关的内容 --------------------------------------------------

    /**
     * 对内容区分简繁体的字段确定个版本（避免传输消耗）
     */
    public void changeLang(Language lang)
    {
        if (lang.isCH()) return;

        if (lang.isSC()) hantz = "";
        else hanzi = "";

        String l = lang.toString();
        String r = lang.reverse().toString();

        for (var i : mulPy) erasure(i, l, r);
        for (var i : ipaExp) erasure(i, l, r);


        erasure(similar, l, r);
        erasure(pyExplain, l, r);
        erasure(mean, l, r);
        erasure(note, l, r);
        erasure(refer, l, r);
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

    /**
     * 在合并之后只剩下两个参数，直接返回即可
     */
    public List<Pair<String, String>> getMulPyPair()
    {
        List<Pair<String, String>> ans = new ArrayList<>();
        for (var i : mulPy) ans.add(Pair.of(i.get("text"), i.get("content")));
        return ans;
    }

    public List<Triple<String, String, String>> getIpaExpTriple()
    {
        List<Triple<String, String, String>> ans = new ArrayList<>();
        for (var i : ipaExp) ans.add(Triple.of(i.get("text"), i.get("tag"), i.get("content")));
        return ans;
    }

    public List<String> getPyExplainText()
    {
        return pyExplain.get("text");
    }

    public List<String> getMeanText()
    {
        return mean.get("text");
    }

    public List<String> getNoteText()
    {
        return note.get("text");
    }

    //  和转化为数据库表有关的内容 --------------------------------------------------

    public CharEntity transfer()
    {
        CharEntity ans = new CharEntity();

        ans.setHanzi(hanzi);
        ans.setHantz(hantz);
        ans.setStdPy(stdPy);
        ans.setSpecial(special);

        ObjectMapper om = new ObjectMapper();
        ans.setSimilar(toJson(similar, om, emptyScTc));
        ans.setMulPy(toJson(mulPy, om, "{}"));
        ans.setPyExplain(toJson(pyExplain, om, emptyScTc));
        ans.setIpaExp(toJson(ipaExp, om));
        ans.setMean(toJson(mean, om, emptyScTc));
        ans.setNote(toJson(note, om, emptyScTc));
        ans.setRefer(toJson(refer, om, emptyScTc));

        return ans;
    }

}
