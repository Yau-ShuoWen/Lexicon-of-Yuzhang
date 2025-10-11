package com.shuowen.yuzong.dao.domain.Character;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;
import com.shuowen.yuzong.Tool.dataStructure.Language;
import com.shuowen.yuzong.Tool.dataStructure.Status;
import com.shuowen.yuzong.dao.model.Character.CharEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

import static com.shuowen.yuzong.Tool.format.JsonTool.*;

@Data
public abstract class Hanzi<T extends UniPinyin, P extends PinyinStyle>
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
     * 不需要额外参数，只需要一个拼音数组的情况，很多时候并不关心他的其他信息
     */
    public List<String> getMulPyList()
    {
        List<String> ans = new ArrayList<>();
        for (var i : mulPy) ans.add(i.get("content"));
        return ans;
    }

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

    /**
     * @param ipaSE 國際音標的「搜索平臺(Search Engine)」
     */
    protected Hanzi(CharEntity ch, P style, Status statue,
                    Function<Set<T>, Map<T, Map<String, String>>> ipaSE)
    {
        this(ch);
        switch (statue)
        {
            case AllPinyin -> initPinyin(style, true);
            case PinyinIPA ->
            {
                initPinyin(style, false);
                initIPA(ipaSE, false);
            }
            case AllIPA -> initIPA(ipaSE, true);
        }
    }

    protected void initPinyin(P style, Boolean all)
    {
        stdPy = formatting(stdPy, style);
        for (var i : mulPy)
            i.put("content", formatting(i.get("content"), style));

        if (all)
        {
            for (var i : ipaExp)
                i.put("content", formatting(i.get("content"), style));
        }
    }

    protected void initIPA(Function<Set<T>, Map<T, Map<String, String>>> ipaSE, Boolean all)
    {
        Set<T> allPinyin = new HashSet<>();
        if (all)
        {
            allPinyin.add(pinyinOf(stdPy));
            for (var i : mulPy)
                allPinyin.add(pinyinOf(i.get("content")));
        }
        for (var i : ipaExp)
            allPinyin.add(pinyinOf(i.get("content")));


        Map<T, Map<String, String>> ipaMap = ipaSE.apply(allPinyin);

        if (all)
        {
            stdPy = ipaMap.get(pinyinOf(stdPy)).get(dict());
            for (var i : mulPy)
                i.put("content", ipaMap.get(pinyinOf(i.get("content"))).get(dict()));
        }
        for (var i : ipaExp)
            i.put("content", ipaMap.get(pinyinOf(i.get("content"))).get(i.get("tag")));

    }

    protected abstract T pinyinOf(String str);

    protected abstract String formatting(String s, P style);

    protected abstract String dict();

    /**
     * 对内容区分简繁体的字段确定个版本（避免传输消耗）
     */
    public void changeLang(Language lang)
    {
        if (lang.isCH()) return;

        if(lang.isSC()) hantz = "";
        else hanzi = "";
        
        String r = lang.reverse().toString();
        for (var i : mulPy) i.remove(r);
        for (var i : ipaExp) i.remove(r);
        similar.remove(r);
        pyExplain.remove(r);
        mean.remove(r);
        note.remove(r);
        refer.remove(r);
    }

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
