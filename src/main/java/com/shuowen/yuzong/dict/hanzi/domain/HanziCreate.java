package com.shuowen.yuzong.dict.hanzi.domain;

import com.shuowen.yuzong.dict.hanzi.model.HanziEntity;
import com.shuowen.yuzong.linguistics.util.KeyboardPinyin;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.json.JsonTool;
import com.shuowen.yuzong.util.text.ScTcText;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/** 批量初始化汉字。 */
@Data
@NoArgsConstructor
public class HanziCreate
{
    private ScTcText text;
    private KeyboardPinyin pinyin;

    public List<HanziEntity> checkAndTransfer(Dialect dialect)
    {
        if (text.length() == 0) return List.of();
        var normalized = dialect.checkAndCreatePinyin(pinyin);
        var item = new HanziPronunciation(
                KeyboardPinyin.of(normalized.toDatabasePinyin().toString(true)),
                normalized.getWeight(),
                new ScTcText("文读", "文讀"),
                List.of()
        );
        String pinyinJson = JsonTool.toJson(List.of(item), "[]");

        List<HanziEntity> result = new ArrayList<>();
        for (int i = 0; i < text.length(); i++)
        {
            var entity = new HanziEntity();
            entity.setSc(text.getSc().at(i));
            entity.setTc(text.getTc().at(i));
            entity.setPinyin(pinyinJson);
            entity.setSpecial(0);
            entity.setNote("[]");
            entity.setStatus(1);
            result.add(entity);
        }
        return result;
    }
}
