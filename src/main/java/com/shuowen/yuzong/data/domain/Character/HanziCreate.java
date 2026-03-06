package com.shuowen.yuzong.data.domain.Character;

import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import com.shuowen.yuzong.Tool.dataStructure.ErrorInfo;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinChecker;
import com.shuowen.yuzong.data.model.Character.HanziEntity;
import com.shuowen.yuzong.data.model.Character.HanziPinyin;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量初始化内容
 */
//TODO
@Data
public class HanziCreate
{
    String sc;
    String tc;
    String pinyin;

    public HanziCreate()
    {
    }

    public void check(Dialect d)
    {
//        final var empty = ErrorInfo.of("内容不能为空。Can't be empty");
//        final var sizeOne = ErrorInfo.of("汉字栏，只能填入一个字。Only can have one character.");
//        final var ScTcLen = ErrorInfo.of("简体繁体长度不相等。The lengths of SCTC are not equal.");
//
//        if (!StringTool.isTrimValid(sc, tc, pinyin))
//            throw new IllegalArgumentException("簡體字、繁體字、主拼音不可以缺少");
//
//        if (!UString.isLenEqual(sc, tc))
//            throw new IllegalArgumentException("簡體字、繁體字数目不对应");
//
//        PinyinChecker.strictly(pinyin, d);
    }

    public Pair<List<HanziEntity>, HanziPinyin> transfer()
    {
        String emptyScTc = "{\"sc\": [], \"tc\": []}";

        List<HanziEntity> left = new ArrayList<>();
        UString sc = new UString(this.sc);
        UString tc = new UString(this.tc);
        for (var i = 0; i < sc.length(); i++)
        {
            var tmp = new HanziEntity();
            tmp.setSc(sc.at(i));
            tmp.setTc(tc.at(i));
            tmp.setMainPy(pinyin);
            // 非关键内容使用默认值代替
            tmp.setSpecial(0);
            tmp.setIpa("[]");
            tmp.setMean(emptyScTc);
            tmp.setNote(emptyScTc);
            tmp.setRefer(emptyScTc);

            left.add(tmp);
        }

        HanziPinyin right = new HanziPinyin();
        {
            right.setSc("文读");
            right.setTc("文讀");
            right.setPinyin(pinyin);
            right.setSort(1);
        }
        return Pair.of(left, right);
    }
}
