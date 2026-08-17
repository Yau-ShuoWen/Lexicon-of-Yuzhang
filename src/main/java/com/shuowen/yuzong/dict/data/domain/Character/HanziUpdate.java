package com.shuowen.yuzong.dict.data.domain.Character;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.dict.data.model.Character.HanziEntity;
import com.shuowen.yuzong.dict.data.model.Character.HanziPinyin;
import com.shuowen.yuzong.dict.data.model.Character.HanziSimilar;
import com.shuowen.yuzong.dict.data.model.Character.MdrChar;
import com.shuowen.yuzong.linguistics.util.KeyboardPinyin;
import com.shuowen.yuzong.util.ext.list.ListTool;
import com.shuowen.yuzong.util.ext.other.ObjectTool;
import com.shuowen.yuzong.util.text.ScTcChar;
import com.shuowen.yuzong.util.text.ScTcText;
import com.shuowen.yuzong.util.text.TextPinyinIPA;
import com.shuowen.yuzong.util.tuple.Quadruple;
import com.shuowen.yuzong.util.tuple.Range;
import com.shuowen.yuzong.util.tuple.Twin;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

import static com.shuowen.yuzong.util.json.JsonTool.readJson;
import static com.shuowen.yuzong.util.json.JsonTool.toJson;

@Data
public class HanziUpdate
{
    private Integer id;
    private ScTcChar hanzi;
    private KeyboardPinyin mainPy;
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

        public HanziSimilar transfer(int charId)
        {
            var ans = new HanziSimilar();
            ans.setId(id);
            ans.setCharId(charId);
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
        KeyboardPinyin pinyin; // 读音变体
        Integer sort;   // 优先等级

        public PinyinData(HanziPinyin py, Dialect d)
        {
            id = py.getId();
            tag = new ScTcText(py.getSc(), py.getTc());
            pinyin = d.trustedCreatePinyin(py.getPinyin()).toKeyboardPinyin();
            sort = py.getSort();
        }

        public HanziPinyin checkPinyinAndTransfer(Dialect d, int charId)
        {
            var ans = new HanziPinyin();
            ans.setId(id);
            ans.setCharId(charId);
            ans.setSc(tag.getSc().toString());
            ans.setTc(tag.getTc().toString());
            ans.setPinyin(d.checkAndCreatePinyin(pinyin).toDatabasePinyin().toString(true));
            ans.setSort(sort);
            return ans;
        }
    }

    private List<Similar> similar;
    private List<PinyinData> variantPy;
    private List<MdrChar> mandarin;

    private List<Twin<ScTcText>> note;

    private Integer status;

    public HanziUpdate()
    {
    }

    public HanziUpdate(Dialect d, HanziEntity ch, List<HanziSimilar> sim, List<HanziPinyin> py, List<MdrChar> mdr)
    {
        id = ch.getId();
        hanzi = new ScTcChar(ch.getSc(), ch.getTc());
        mainPy = d.trustedCreatePinyin(ch.getMainPy()).toKeyboardPinyin();
        special = ch.getSpecial();

        // 其他表的查询
        similar = ListTool.mapping(sim, Similar::new);
        variantPy = ListTool.mapping(py, i -> new PinyinData(i, d));
        mandarin = mdr;

        // 表的复杂字段

        note = ListTool.mapping(
                readJson(ch.getNote(), new TypeReference<List<Map<String, ScTcText>>>() {}),
                i -> Twin.of(i.get("tag"), i.get("content").map(str -> TextPinyinIPA.transferPinyin(str, d, true))));

        status = ch.getStatus();
    }

    public Quadruple<HanziEntity, List<HanziSimilar>, List<HanziPinyin>, List<MdrChar>>
    checkAndTransfer(Dialect d)
    {
        HanziEntity ch = new HanziEntity();

        ch.setId(id);
        ch.setSc(hanzi.getSc().toString());
        ch.setTc(hanzi.getTc().toString());

        var dPinyin = d.checkAndCreatePinyin(mainPy);
        ch.setMainPy(dPinyin.toDatabasePinyin().toString(true));
        ch.setPyCode(dPinyin.getWeight());

        ObjectTool.asserts(Range.close(0, 4).contains(special), "");
        ch.setSpecial(special);

        var sim = ListTool.mapping(similar, i -> i.transfer(id));

        var py = ListTool.mapping(variantPy, i -> i.checkPinyinAndTransfer(d, id));

        var mdr = mandarin;
        ListTool.handle(mdr, i -> i.setDialectId(id));

        ch.setNote(toJson(ListTool.mapping(note,
                i -> Map.of("tag", i.getLeft(),
                        "content", i.getRight().map(str -> TextPinyinIPA.transferPinyin(str, d, false))
                )
        ), "[]"));

        ch.setStatus(status);

        return Quadruple.of(ch, sim, py, mdr);
    }
}