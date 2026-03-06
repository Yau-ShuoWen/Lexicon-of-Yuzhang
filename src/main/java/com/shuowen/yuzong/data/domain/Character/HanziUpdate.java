package com.shuowen.yuzong.data.domain.Character;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Tool.JavaUtilExtend.NullTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import com.shuowen.yuzong.Tool.dataStructure.ErrorInfo;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinChecker;
import com.shuowen.yuzong.data.model.Character.HanziEntity;
import com.shuowen.yuzong.data.model.Character.HanziPinyin;
import com.shuowen.yuzong.data.model.Character.HanziSimilar;
import com.shuowen.yuzong.data.model.Character.MdrChar;
import lombok.Data;

import java.util.*;

import static com.shuowen.yuzong.Tool.format.JsonTool.readJson;
import static com.shuowen.yuzong.Tool.format.JsonTool.toJson;

@Data
public class HanziUpdate
{
    protected Integer id;
    protected String sc;
    protected String tc;
    protected String mainPy;
    protected Integer special;

    protected List<HanziSimilar> similar = new ArrayList<>();
    protected List<HanziPinyin> variantPy = new ArrayList<>();
    protected List<MdrChar> mandarin = new ArrayList<>();
    protected List<Pair<String, String>> ipa = new ArrayList<>();
    protected List<Pair<String, String>> mean = new ArrayList<>();
    protected List<Pair<Pair<String, String>, Pair<String, String>>> note = new ArrayList<>();
    /* ↑这个的结构是：
     * 外面的Pair负责分简繁，Left简体、Right繁体；
     * 里面的Pair负责区分标签和内容，Left标签、Right内容。
     */

    public HanziUpdate()
    {
    }

    public HanziUpdate(HanziEntity ch, List<HanziSimilar> sim, List<HanziPinyin> py, List<MdrChar> mdr)
    {
        id = ch.getId();
        sc = ch.getSc();
        tc = ch.getTc();
        mainPy = ch.getMainPy();
        special = ch.getSpecial();

        similar.addAll(sim);
        variantPy.addAll(py);
        mandarin.addAll(mdr);

        readDAO(ch);
    }

    /**
     * 在数据发回来的时候对数据检查
     */
    public void check(Dialect d)
    {
        final var empty = ErrorInfo.of("内容不能为空。Can't be empty");
        final var sizeOne = ErrorInfo.of("汉字栏，只能填入一个字。Only can have one character.");
        final var ScTcLen = ErrorInfo.of("简体繁体长度不相等。The lengths of SCTC are not equal.");

        // 《批评和自我批评》
        // 1. 简体字、繁体字、主拼音 是必须字段
        // 2. 简体字、繁体字 是单个字符
        // 3. 主拼音符合格式
        // 4. 特殊性标记必须在范围里面

        StringTool.checkTrimValid(empty, sc, tc, mainPy);
        UString.checkUChar(sizeOne, sc, tc);
        PinyinChecker.strictly(mainPy, d);
        NullTool.checkNotNull(special);


        // 5. 相似汉字单个字符
        // 6. 读音变体里，简繁标签不为空，拼音不为空且符合格式
        // 7. 含义里，简繁版本不为空且

        for (var i : similar)
        {
            UString.checkUChar(sizeOne, i.getSc(), i.getTc());
        }
        for (var i : variantPy)
        {
            StringTool.checkTrimValid(empty, i.getSc(), i.getTc(), i.getPinyin());
            PinyinChecker.strictly(i.getPinyin(), d);
        }
        for (var i : mean)
        {
            StringTool.checkTrimValid(empty, i.getLeft(), i.getRight());
            UString.checkLenEqual(ScTcLen, i.getLeft(), i.getRight());
        }
        for (var i : note)
        {
            StringTool.checkTrimValid(empty, i.getLeft().getLeft(), i.getLeft().getRight(),
                    i.getRight().getLeft(), i.getRight().getRight());
            UString.checkLenEqual(ScTcLen, i.getLeft().getLeft(), i.getRight().getLeft());
            UString.checkLenEqual(ScTcLen, i.getLeft().getRight(), i.getRight().getRight());
        }
    }

    public HanziEntity transfer()
    {
        HanziEntity ans = new HanziEntity();

        ans.setId(id);
        ans.setSc(sc);
        ans.setTc(tc);
        ans.setMainPy(mainPy);
        ans.setSpecial(special);

        toDao(ans);

        return ans;
    }

    public static HanziUpdate of(HanziEntity ch, List<HanziSimilar> sim, List<HanziPinyin> py, List<MdrChar> mdr)
    {
        return new HanziUpdate(ch, sim, py, mdr);
    }

    private void readDAO(HanziEntity ch)
    {
        ObjectMapper om = new ObjectMapper();

        {
            List<Map<String, String>> tmp = readJson(ch.getIpa(), new TypeReference<>() {}, om);
            for (var i : tmp)
                ipa.add(Pair.of(i.get("tag"), i.get("content")));
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

    private void toDao(HanziEntity ch)
    {
        // 空的结构
        String emptyScTc = "{\"sc\": [], \"tc\": []}";
        ObjectMapper om = new ObjectMapper();

        {
            List<Map<String, String>> tmp = new ArrayList<>();
            for (var i : ipa)
            {
                Map<String, String> t = new HashMap<>();
                t.put("tag", i.getLeft());
                t.put("content", i.getRight());
                tmp.add(t);
            }
            ch.setIpa(toJson(tmp, om, "[]"));
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