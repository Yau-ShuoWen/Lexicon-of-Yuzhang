package com.shuowen.yuzong.data.domain.IPA;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Linguistics.Scheme.SPinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.data.domain.Reference.DictCode;
import com.shuowen.yuzong.data.model.IPA.IPASyllEntity;
import lombok.Data;

import java.util.*;

import static com.shuowen.yuzong.Tool.format.JsonTool.readJson;
import static com.shuowen.yuzong.Tool.format.JsonTool.toJson;


/**
 * 音节领域模型类
 */
@Data
public class Yinjie
{
    protected SPinyin pinyin;
    protected Map<DictCode, String> info;
    protected String code;

    private Yinjie(IPASyllEntity ipa)
    {
        pinyin = SPinyin.of(ipa.getStandard());
        code = ipa.getCode();
        info = readJson(ipa.getInfo(), new TypeReference<>() {}, new ObjectMapper());
    }

    public static List<Yinjie> listOf(List<IPASyllEntity> list)
    {
        return ListTool.mapping(list, Yinjie::new);
    }

    public static Map<String, Yinjie> mapOf(Set<IPASyllEntity> set)
    {
        Map<String, Yinjie> map = new HashMap<>();
        for (var i : set)
        {
            if (i == null) continue;
            map.put(i.getStandard(), new Yinjie(i));
        }
        return map;
    }

    public String getInfo(DictCode dict)
    {
        // 如果等于"-"，改成null，这是数据库明示这里没有数据的方式
        return "-".equals(info.get(dict)) ? null : info.get(dict);
    }

    /**
     * 从声母和韵母拼接而成
     */
    private Yinjie(Shengyun initial, Shengyun last)
    {
        pinyin = SPinyin.of(initial.pinyin + last.pinyin);
        code = (initial.code + last.code).replace("~", "");
        info = new HashMap<>();
        for (var i : initial.getInfo().keySet())
        {
            String ipa = initial.getInfo().get(i) + last.getInfo().get(i);
            if (ipa.contains("-")) ipa = "-";
            info.put(i, ipa);
        }
    }


    public static Maybe<Yinjie> merge(Maybe<Shengyun> initial, Maybe<Shengyun> last)
    {
        if (initial.isEmpty() || last.isEmpty()) return Maybe.nothing();
        return Maybe.exist(new Yinjie(initial.getValue(), last.getValue()));
    }


    public IPASyllEntity transfer()
    {
        IPASyllEntity ans = new IPASyllEntity();
        ans.setStandard(pinyin.toString());
        ans.setCode(code);
        ans.setInfo(toJson(info, new ObjectMapper()));
        return ans;
    }
}
