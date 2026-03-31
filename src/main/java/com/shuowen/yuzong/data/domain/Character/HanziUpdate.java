package com.shuowen.yuzong.data.domain.Character;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Linguistics.Scheme.SPinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.NumberTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ObjectTool;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcChar;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Quadruple;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Twin;
import com.shuowen.yuzong.data.model.Character.HanziEntity;
import com.shuowen.yuzong.data.model.Character.HanziPinyin;
import com.shuowen.yuzong.data.model.Character.HanziSimilar;
import com.shuowen.yuzong.data.model.Character.MdrChar;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

import static com.shuowen.yuzong.Tool.format.JsonTool.readJson;
import static com.shuowen.yuzong.Tool.format.JsonTool.toJson;

@Data
public class HanziUpdate
{
    private Integer id;
    private ScTcChar hanzi;
    private SPinyin mainPy;
    private Integer special;

    @Data
    @NoArgsConstructor
    public static class Similar
    {
        Integer id;     // 新增的内容id设置为0
        ScTcChar text;

        public Similar(HanziSimilar sim)
        {
            id = sim.getId();
            text = new ScTcChar(sim.getSc(), sim.getTc());
        }

        public HanziSimilar transfer()
        {
            var ans = new HanziSimilar();
            ans.setId(id);
            ans.setSc(text.getSc().toString());
            ans.setTc(text.getTc().toString());
            return ans;
        }
    }

    @Data
    @NoArgsConstructor
    public static class PinyinData
    {
        Integer id;     // 新增的内容id设置为0
        ScTcText tag;   // 标签，简繁
        SPinyin pinyin; // 读音变体
        Integer sort;   // 优先等级

        public PinyinData(HanziPinyin py)
        {
            id = py.getId();
            tag = new ScTcText(py.getSc(), py.getTc());
            pinyin = SPinyin.of(py.getPinyin());
            sort = py.getSort();
        }

        public void checkPinyin(Dialect d)
        {
            d.checkAndCreatePinyin(pinyin); // 只检查不用
        }

        public HanziPinyin transfer()
        {
            var ans = new HanziPinyin();
            ans.setId(id);
            ans.setSc(tag.getSc().toString());
            ans.setTc(tag.getTc().toString());
            ans.setPinyin(pinyin.toString());
            ans.setSort(sort);
            return ans;
        }
    }

    private List<Similar> similar;
    private List<PinyinData> variantPy;
    private List<MdrChar> mandarin;

    private List<Pair<String, String>> ipa = new ArrayList<>();
    private List<Twin<ScTcText>> note;

    public HanziUpdate()
    {
    }

    public HanziUpdate(HanziEntity ch, List<HanziSimilar> sim, List<HanziPinyin> py, List<MdrChar> mdr)
    {
        id = ch.getId();
        hanzi = new ScTcChar(ch.getSc(), ch.getTc());
        mainPy = SPinyin.of(ch.getMainPy());
        special = ch.getSpecial();

        // 其他表的查询
        similar = ListTool.mapping(sim, Similar::new);
        variantPy = ListTool.mapping(py, PinyinData::new);
        mandarin = mdr;

        // 表的复杂字段

        ObjectMapper om = new ObjectMapper();

        // @desprate，之后新功能上之后需要完全删掉，所以不重构
        {
            List<Map<String, String>> tmp = readJson(ch.getIpa(), new TypeReference<>() {}, om);
            for (var i : tmp)
                ipa.add(Pair.of(i.get("tag"), i.get("content")));
        }

        note = ListTool.mapping(
                readJson(ch.getNote(), new TypeReference<List<Map<String, ScTcText>>>() {}, om),
                i -> Twin.of(i.get("tag"), i.get("content")));
    }

    public Quadruple<HanziEntity, List<HanziSimilar>, List<HanziPinyin>, List<MdrChar>>
    checkAndTransfer(Dialect d)
    {
        HanziEntity ch = new HanziEntity();


        ch.setId(id);
        ch.setSc(hanzi.getSc().toString());
        ch.setTc(hanzi.getTc().toString());

        var dPinyin = d.checkAndCreatePinyin(mainPy);
        ch.setMainPy(mainPy.toString());
        ch.setPyCode(dPinyin.getWeight());

        ObjectTool.asserts(NumberTool.closeBetween(special, 0, 3),
                "");
        ch.setSpecial(special);

        var sim = ListTool.mapping(similar, Similar::transfer);

        ListTool.handle(variantPy, i -> i.checkPinyin(d));
        var py = ListTool.mapping(variantPy, PinyinData::transfer);

        var mdr = mandarin;

        // @desprate，之后新功能上之后需要完全删掉，所以不重构
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

        ch.setNote(toJson(
                ListTool.mapping(note, i -> Map.of("tag", i.getLeft(), "content", i.getRight())),
                om, "[]"));

        return Quadruple.of(ch, sim, py, mdr);
    }
}