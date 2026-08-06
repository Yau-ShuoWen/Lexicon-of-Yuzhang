package com.shuowen.yuzong.dict.data.domain.Character;

import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.dict.data.model.Character.HanziEntity;
import com.shuowen.yuzong.dict.data.model.Character.HanziPinyin;
import com.shuowen.yuzong.linguistics.util.KeyboardPinyin;
import com.shuowen.yuzong.util.text.ScTcText;
import com.shuowen.yuzong.util.tuple.Pair;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量初始化内容
 */
@Data
@NoArgsConstructor
public class HanziCreate
{
    ScTcText text;
    KeyboardPinyin pinyin;

    public Pair<List<HanziEntity>, HanziPinyin> checkAndTransfer(Dialect d)
    {
        if (text.length() == 0) return Pair.of(List.of(), null); // 只要保证了列表为空，就不会循环

        var dPinyin = d.checkAndCreatePinyin(pinyin);

        List<HanziEntity> left = new ArrayList<>();
        var sc = text.getSc();
        var tc = text.getTc();
        for (int i = 0; i < sc.length(); i++)
        {
            var tmp = new HanziEntity();
            tmp.setSc(sc.at(i));
            tmp.setTc(tc.at(i));
            tmp.setMainPy(dPinyin.toDatabasePinyin().toString(true));
            tmp.setPyCode(dPinyin.getWeight());
            // 非关键内容使用默认值代替
            tmp.setSpecial(0);
            tmp.setNote("[]");
            tmp.setStatus(1);

            left.add(tmp);
        }

        HanziPinyin right = new HanziPinyin();
        {
            right.setSc("文读");
            right.setTc("文讀");
            right.setPinyin(pinyin.toString());
            right.setSort(1);
        }
        return Pair.of(left, right);
    }
}
