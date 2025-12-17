package com.shuowen.yuzong.data.domain.Character;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Tool.JavaUtilExtend.NullTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.data.model.Character.CharEntity;
import com.shuowen.yuzong.data.model.Character.CharPinyin;
import com.shuowen.yuzong.data.model.Character.CharSimilar;
import com.shuowen.yuzong.data.model.Character.CharMdr;
import lombok.Data;

import java.util.*;

import static com.shuowen.yuzong.Tool.format.JsonTool.readJson;
import static com.shuowen.yuzong.Tool.format.JsonTool.toJson;

@Data
public class HanziEdit
{
    protected Integer id;
    protected String hanzi;
    protected String hantz;
    protected String stdPy;
    protected Integer special;

    protected List<CharSimilar> similar = new ArrayList<>();
    protected List<CharPinyin> mulPy = new ArrayList<>();
    protected List<CharMdr> mandarin = new ArrayList<>();
    protected List<Pair<String, String>> ipaExp = new ArrayList<>();
    protected List<Pair<String, String>> mean = new ArrayList<>();
    protected List<Pair<Pair<String, String>, Pair<String, String>>> note = new ArrayList<>();
    /* ↑这个的结构是：
     * 外面的Pair负责分简繁，Left简体、Right繁体；
     * 里面的Pair负责区分标签和内容，Left标签、Right内容。
     */

    public HanziEdit()
    {
    }

    public HanziEdit(CharEntity ch, List<CharSimilar> sim, List<CharPinyin> py, List<CharMdr> mdr)
    {
        id = ch.getId();
        hanzi = ch.getHanzi();
        hantz = ch.getHantz();
        stdPy = ch.getStdPy();
        special = ch.getSpecial();

        similar.addAll(sim);
        mulPy.addAll(py);
        mandarin.addAll(mdr);

        readDAO(ch);
    }

    /**
     * 在数据发回来的时候对数据检查
     */
    public void check()
    {
        // 《批评和自我批评》

        if (!StringTool.isTrimValid(hanzi, hantz, stdPy))
            throw new IllegalArgumentException("簡體字、繁體字、主拼音不可以缺少");

        if (!UString.isChar(hanzi, hantz))
            throw new IllegalArgumentException("输入的字不止一个");

        NullTool.checkSingleNotNull(special);

        for (var i : similar)
        {
            StringTool.checkTrimValid(i.getHanzi(), i.getHantz());
            UString.checkChar(i.getHanzi(), i.getHantz());
        }
        for (var i : mulPy)
        {
            StringTool.checkTrimValid(i.getSc(), i.getTc(), i.getPinyin());
            UString.checkChar(i.getSc(), i.getTc());
        }
        for (var i : mean)
        {
            StringTool.checkTrimValid(i.getLeft(), i.getRight());
            UString.checkLenEqual(i.getLeft(), i.getRight());
        }
        for (var i : note)
        {
            StringTool.checkTrimValid(i.getLeft().getLeft(), i.getLeft().getRight(),
                    i.getRight().getLeft(), i.getRight().getRight());
            UString.checkLenEqual(i.getLeft().getLeft(), i.getRight().getLeft());
            UString.checkLenEqual(i.getLeft().getRight(), i.getRight().getRight());
        }
    }

    public CharEntity transfer()
    {
        CharEntity ans = new CharEntity();

        ans.setId(id);
        ans.setHanzi(hanzi);
        ans.setHantz(hantz);
        ans.setStdPy(stdPy);
        ans.setSpecial(special);

        toDao(ans);

        return ans;
    }

    public static HanziEdit of(CharEntity ch, List<CharSimilar> sim, List<CharPinyin> py, List<CharMdr> mdr)
    {
        return new HanziEdit(ch, sim, py, mdr);
    }

    private void readDAO(CharEntity ch)
    {
        ObjectMapper om = new ObjectMapper();

        {
            List<Map<String, String>> tmp = readJson(ch.getIpaExp(), new TypeReference<>() {}, om);
            for (var i : tmp)
                ipaExp.add(Pair.of(i.get("tag"), i.get("content")));
        }

        {
            Map<String, List<String>> tmp = readJson(ch.getMean(), new TypeReference<>() {}, om);
            for (int i = 0; i < tmp.get("sc").size(); i++)
                mean.add(Pair.of(tmp.get("sc").get(i), tmp.get("tc").get(i)));
        }

        {
            Map<String, List<Map<String, String>>> tmp = readJson(ch.getNote(), new TypeReference<>() {}, om);
            for (int i = 0; i < tmp.get("sc").size(); i++)
                note.add(Pair.of(
                        Pair.of(tmp.get("sc").get(i).get("tag"), tmp.get("sc").get(i).get("content")),
                        Pair.of(tmp.get("tc").get(i).get("tag"), tmp.get("tc").get(i).get("content"))
                ));
        }
    }

    private void toDao(CharEntity ch)
    {
        // 空的结构
        String emptyScTc = "{\"sc\": [], \"tc\": []}";
        ObjectMapper om = new ObjectMapper();

        {
            List<Map<String, String>> tmp = new ArrayList<>();
            for (var i : ipaExp)
            {
                Map<String, String> t = new HashMap<>();
                t.put("tag", i.getLeft());
                t.put("content", i.getRight());
                tmp.add(t);
            }
            ch.setIpaExp(toJson(tmp, om, "[]"));
        }

        {
            Map<String, List<String>> tmp = new HashMap<>();
            tmp.put("sc", new ArrayList<>());
            tmp.put("tc", new ArrayList<>());
            for (var i : mean)
            {
                tmp.get("sc").add(i.getLeft());
                tmp.get("tc").add(i.getRight());
            }
            ch.setMean(toJson(tmp, om, emptyScTc));
        }

        {
            Map<String, List<Map<String, String>>> tmp = new HashMap<>();
            tmp.put("sc", new ArrayList<>());
            tmp.put("tc", new ArrayList<>());
            for (var i : note)
            {
                Map<String, String> t = new HashMap<>();
                t.put("tag", i.getLeft().getLeft());
                t.put("content", i.getLeft().getRight());
                tmp.get("sc").add(t);

                t = new HashMap<>();
                t.put("tag", i.getRight().getLeft());
                t.put("content", i.getRight().getRight());
                tmp.get("tc").add(t);
            }
            ch.setNote(toJson(tmp, om, emptyScTc));
        }

        ch.setRefer(emptyScTc);
    }
}