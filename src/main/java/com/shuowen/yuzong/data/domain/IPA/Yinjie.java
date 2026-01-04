package com.shuowen.yuzong.data.domain.IPA;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.data.model.IPA.IPASyllableEntity;
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
    protected String pinyin;
    protected Map<String, String> info;
    protected String code;

    private Yinjie(IPASyllableEntity ipa)
    {
        pinyin = ipa.getStandard();
        code = ipa.getCode();
        info = readJson(ipa.getInfo(), new TypeReference<>() {}, new ObjectMapper());
    }

    public static Maybe<Yinjie> tryOf(IPASyllableEntity ipa)
    {
        if (ipa == null) return Maybe.nothing();
        else return Maybe.exist(new Yinjie(ipa));
    }

    public static List<Yinjie> listOf(List<IPASyllableEntity> list)
    {
        return ListTool.mapping(list, Yinjie::new);
    }

    public Maybe<String> getInfo(String dict)
    {
        // 如果等于"-"，改成null，因为Maybe可以处理null
        var ans = "-".equals(info.get(dict)) ? null : info.get(dict);
        return Maybe.uncertain(ans);
    }


    /**
     * 从声母和韵母拼接而成
     */
    private Yinjie(Shengyun initial, Shengyun last)
    {
        pinyin = initial.pinyin + last.pinyin;
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


    public IPASyllableEntity transfer()
    {
        IPASyllableEntity ans = new IPASyllableEntity();
        ans.setStandard(pinyin);
        ans.setCode(code);
        ans.setInfo(toJson(info, new ObjectMapper()));
        return ans;
    }
}
