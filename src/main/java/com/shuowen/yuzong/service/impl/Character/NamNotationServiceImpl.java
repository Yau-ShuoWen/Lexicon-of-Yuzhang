package com.shuowen.yuzong.service.impl.Character;

import com.shuowen.yuzong.Linguistics.Mandarin.HanPinyin;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.dao.mapper.Character.NamNotationMapper;
import com.shuowen.yuzong.service.interfaces.Character.NotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NamNotationServiceImpl implements NotationService
{
    @Autowired
    private NamNotationMapper m;

    public List<Pair<String, String>> aaa(String s)
    {
        List<String> pinyin = HanPinyin.txtPinyin(s);
        List<String> tmp = new ArrayList<>();
        for (int i = 0; i < pinyin.size(); i++)
        {
            if (!"none5".equals(pinyin.get(i)))
                tmp.add(s.charAt(i) + pinyin.get(i));
        }

        Map<String, String> map = new HashMap<>();
        for (var i : m.selectMdrDialectChars(tmp))
            map.put(i.getHpComb(), i.getStdPy());

        List<Pair<String, String>> ans = new ArrayList<>();
        for (int i = 0; i < pinyin.size(); i++)
        {
            ans.add(Pair.of("" + s.charAt(i), ("none5".equals(pinyin.get(i))) ? "-" :
                            map.getOrDefault(s.charAt(i) + pinyin.get(i), "-")));
        }
        return ans;
    }
}