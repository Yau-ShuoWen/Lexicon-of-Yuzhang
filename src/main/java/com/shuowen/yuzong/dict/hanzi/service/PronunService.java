package com.shuowen.yuzong.dict.hanzi.service;

import com.shuowen.yuzong.dict.hanzi.domain.HanziPronunciation;
import com.shuowen.yuzong.dict.hanzi.domain.MdrTool;
import com.shuowen.yuzong.dict.hanzi.mapper.PronunMapper;
import com.shuowen.yuzong.dict.hanzi.model.MdrChar;
import com.shuowen.yuzong.linguistics.Mandarin.HanPinyin;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.err.InvalidPinyinException;
import com.shuowen.yuzong.util.ext.list.ListTool;
import com.shuowen.yuzong.util.text.UChar;
import com.shuowen.yuzong.util.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

    /**
     * 通过简体字和繁体字获得选项
     */
    public List<MdrChar> getHanziMenu(String sc, String tc, Dialect d)
    {
        var list = getRawCandidates(sc, tc);
        ListTool.handle(list, i -> i.setInfo(MdrTool.initWithPinyin(i.getInfo())));
        return list;
    }

    public List<MdrChar> getRawCandidates(String sc, String tc)
    {
        return m.getInfoByScTc(sc, tc);
    }

    /** 校验内嵌映射：ID 存在、当前汉字内不重复、没有被其他汉字占用。 */
    public void validateMappings(List<HanziPronunciation> pinyin, Integer hanziId,
                                 String sc, String tc, Dialect d)
    {
        var ids = pinyin.stream().flatMap(i -> i.getMandarin().stream()).toList();
        if (ids.isEmpty()) return;
        if (new HashSet<>(ids).size() != ids.size())
            throw new IllegalArgumentException("同一个普通话读音不能分配给多个方言拼音");
        var candidateIds = new HashSet<>(ListTool.mapping(m.getInfoByScTc(sc, tc), MdrChar::getMandarinId));
        if (!candidateIds.containsAll(ids))
            throw new IllegalArgumentException("普通话读音不属于当前汉字");
        int excludeId = hanziId == null ? -1 : hanziId;
        if (!m.getMappedMandarinIds(ids, excludeId, d.toString()).isEmpty())
            throw new IllegalArgumentException("普通话读音已经被其他汉字拼音占用");
    }

    public List<Pair<HanPinyin, List<UChar>>> getHanzisByPinyin(String text)
    {

        List<Pair<HanPinyin, List<UChar>>> ans = new ArrayList<>();
        for (int i = 0; i <= 4; i++)
        {
            try
            {
                var py = HanPinyin.of(text + i);
                var list = ListTool.mapping(m.findHanziFreqByPinyin(py.getSyll(), py.getTone()), UChar::of);
                ans.add(Pair.of(py, list));
            } catch (InvalidPinyinException e)
            {
                return null;
            }
        }
        return ans;
    }
}
