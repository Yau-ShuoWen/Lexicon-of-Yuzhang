package com.shuowen.yuzong.data.domain.Character;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Quadruple;
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
    protected List<Quadruple<String, String, String, String>> ipaExp = new ArrayList<>();
    protected List<Pair<String, String>> mean = new ArrayList<>();
    protected List<Pair<Pair<String, String>, Pair<String, String>>> note = new ArrayList<>();

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
                ipaExp.add(Quadruple.of(i.get("sc"), i.get("tc"), i.get("tag"), i.get("content")));
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

    private void toDao(CharEntity left)
    {
        // 空的结构
        String emptyScTc = "{\"sc\": [], \"tc\": []}";
        ObjectMapper om = new ObjectMapper();

        {
            List<Map<String, String>> tmp = new ArrayList<>();
            for (var i : ipaExp)
            {
                Map<String, String> t = new HashMap<>();
                t.put("sc", i.getAlpha());
                t.put("tc", i.getBeta());
                t.put("tag", i.getGamma());
                t.put("content", i.getDelta());
                tmp.add(t);
            }
            left.setIpaExp(toJson(tmp, om, "[]"));
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
            left.setMean(toJson(tmp, om, emptyScTc));
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
            left.setNote(toJson(tmp, om, emptyScTc));
        }

        left.setRefer(emptyScTc);
    }
}