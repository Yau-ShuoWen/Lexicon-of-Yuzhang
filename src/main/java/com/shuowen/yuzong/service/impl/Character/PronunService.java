package com.shuowen.yuzong.service.impl.Character;

import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.data.domain.Character.MdrTool;
import com.shuowen.yuzong.data.mapper.Character.PronunMapper;
import com.shuowen.yuzong.data.model.Character.MdrChar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PronunService
{
    @Autowired
    private PronunMapper m;

    //    /**
    //     * 标注读音
    //     */
    ////    public List<Pair<String, String>> notation(String s, Dialect d)
    //    {
    //        List<String> pinyin = HanPinyin.textPinyin(s);
    //        List<String> tmp = new ArrayList<>();
    //        for (int i = 0; i < pinyin.size(); i++)
    //        {
    //            if (!"none5".equals(pinyin.get(i)))
    //                tmp.add(s.charAt(i) + " " + pinyin.get(i));
    //        }
    //
    //        Map<String, String> map = new HashMap<>();
    //        for (var i : m.selectMandarinByChars(tmp, d.toString()))
    //            map.put(i.getInfo(), i.getStdPy());
    //
    //        List<Pair<String, String>> ans = new ArrayList<>();
    //        for (int i = 0; i < pinyin.size(); i++)
    //        {
    //            ans.add(Pair.of("" + s.charAt(i), ("none5".equals(pinyin.get(i))) ? "-" :
    //                    map.getOrDefault(s.charAt(i) + pinyin.get(i), "-")));
    //        }
    //        return ans;
    //    }

    public List<MdrChar> getEdit(int id, Dialect d)
    {
        return MdrTool.initWithPinyin(m.getInfoByDialectId(id, d.toString()));
    }

    public void edit(List<MdrChar> ch, Dialect d)
    {
        if (ch.isEmpty()) return;

        m.clearMapByDialectId(ch.get(0).getDialectId(), d.toString());

        var conflict = m.getInfoByMandarinId(
                ListTool.mapping(ch, MdrChar::getMandarinId),false, d.toString());
        if (!conflict.isEmpty())
        {
            var info = ListTool.mapping(conflict, i -> MdrTool.initWithPinyin(i.getInfo()));
            throw new IllegalArgumentException("普通话信息" + info + "和已有的重复了");
        }

        var data = ListTool.mapping(ch, i -> Pair.of(i.getMandarinId(), i.getDialectId()));
        m.insertMap(data, d.toString());
    }
}
