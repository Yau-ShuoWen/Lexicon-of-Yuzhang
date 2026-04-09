package com.shuowen.yuzong.data.domain.Character;

import com.shuowen.yuzong.Linguistics.Scheme.SPinyin;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.data.model.Character.HanziEntity;
import com.shuowen.yuzong.data.model.Character.HanziPinyin;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * 批量初始化内容
 */
@Data
@NoArgsConstructor
public class HanziCreate
{
    ScTcText text;
    SPinyin pinyin;

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
            tmp.setMainPy(pinyin.toString());
            tmp.setPyCode(dPinyin.getWeight());
            // 非关键内容使用默认值代替
            tmp.setSpecial(0);
            tmp.setIpa("[]");
            tmp.setNote("[]");
            tmp.setRefer("[]");
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
