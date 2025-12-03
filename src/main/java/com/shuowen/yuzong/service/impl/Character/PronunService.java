package com.shuowen.yuzong.service.impl.Character;

import com.shuowen.yuzong.Linguistics.Mandarin.HanPinyin;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.data.mapper.Character.PronunMapper;
import com.shuowen.yuzong.data.model.Character.CharMdr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.shuowen.yuzong.data.domain.Character.MdrTool.settle;

@Service
public class PronunService
{
    @Autowired
    private PronunMapper m;

    /**
     * 标注读音
     */
    public List<Pair<String, String>> notation(String s, Dialect d)
    {
        List<String> pinyin = HanPinyin.txtPinyin(s);
        List<String> tmp = new ArrayList<>();
        for (int i = 0; i < pinyin.size(); i++)
        {
            if (!"none5".equals(pinyin.get(i)))
                tmp.add(s.charAt(i) + " " + pinyin.get(i));
        }

        Map<String, String> map = new HashMap<>();
        for (var i : m.selectMdrDialectChars(tmp, d.toString()))
            map.put(i.getInfo(), i.getStdPy());

        List<Pair<String, String>> ans = new ArrayList<>();
        for (int i = 0; i < pinyin.size(); i++)
        {
            ans.add(Pair.of("" + s.charAt(i), ("none5".equals(pinyin.get(i))) ? "-" :
                    map.getOrDefault(s.charAt(i) + pinyin.get(i), "-")));
        }
        return ans;
    }

    public List<CharMdr> getEdit(int id, Dialect d)
    {
        return settle(m.getMdrInfoByDialectId(id, d.toString()));
    }

    public void edit(List<CharMdr> ch, Dialect d)
    {
        if (ch.isEmpty()) return;
        m.clearMapByDialectId(ch.get(0).getRightId(), d.toString());
        for (CharMdr i : ch) m.insertMap(i.getLeftId(), i.getRightId(), d.toString());
    }

}
