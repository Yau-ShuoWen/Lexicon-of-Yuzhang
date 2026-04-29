package com.shuowen.yuzong.data.domain.Word;

import com.shuowen.yuzong.Linguistics.Scheme.PinyinFormatter;
import com.shuowen.yuzong.Linguistics.Scheme.SPinyins;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.data.model.Word.CiyuEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.shuowen.yuzong.Tool.format.JsonTool.toJson;

@Data
@NoArgsConstructor
public class CiyuCreate
{
    ScTcText text;
    String pys;

    public List<CiyuEntity> checkAndTransfer(Dialect d)
    {
        var txt = text.mapToOther(str ->
                ListTool.mapping(str.split("\n"), UString::of)
        );
        var pinyins = ListTool.mapping(pys.split("\n"), SPinyins::of);

        if (txt.getLeft().size() != pinyins.size()) throw new IllegalArgumentException("词语数量和拼音行数不一样");
        var size = pinyins.size();

        List<CiyuEntity> ans = new ArrayList<>();
        for (int i = 0; i < size; i++)
        {
            UString sc = txt.getLeft().get(i);
            UString tc = txt.getRight().get(i);
            SPinyins py = pinyins.get(i);

            if (sc.length() != py.size()) throw new IllegalArgumentException(
                    String.format("词语：%s %s\n和对应拼音的长度不一样", sc, tc)
            );

            var tmp = new CiyuEntity();
            tmp.setSc(sc.toString());
            tmp.setTc(tc.toString());
            tmp.setSpecial(0);

            tmp.setMainPy(toJson(
                    ListTool.mapping(py.getPinyin(), j ->
                    {
                        var dPinyin = d.checkAndCreatePinyin(j);
                        return PinyinFormatter.toDPinyin(dPinyin, d).toString(true);
                    })
            ));


            tmp.setVariantPy("[]");
            tmp.setMean("[]");

            ans.add(tmp);
        }
        return ans;
    }
}
