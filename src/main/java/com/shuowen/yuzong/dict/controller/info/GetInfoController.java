package com.shuowen.yuzong.dict.controller.info;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.dict.data.mapper.Character.HanziMapper;
import com.shuowen.yuzong.dict.data.mapper.Reference.RefMapper;
import com.shuowen.yuzong.dict.data.mapper.Word.CiyuMapper;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.ext.list.ListTool;
import com.shuowen.yuzong.util.map.KV;
import com.shuowen.yuzong.util.text.ScTcText;
import com.shuowen.yuzong.util.text.UString;
import com.shuowen.yuzong.util.tuple.Twin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.shuowen.yuzong.util.json.JsonTool.readJson;

/**
 * 获得字典信息<br>
 * 不用{@code APIResponse}的原因是，接收一个常量都失败的话，前端有没有那个报错信息都无所谓了
 */
@RestController
@RequestMapping ("/api/info")
public class GetInfoController
{
    @Autowired
    private HanziMapper hz;

    @Autowired
    private CiyuMapper cy;

    @Autowired
    private RefMapper ck;

    @GetMapping ("/get-text/{d}/{l}/{code}")
    public UString welcome(@PathVariable Dialect d, @PathVariable Language l,
                           @PathVariable String code)
    {

        if (code.equals("welcome")) return ScTcText.get(KV.get("website-greeting:" + d), d, l);

        if (code.contains("explain")) return ScTcText.get(KV.get(code), l);

        return UString.of("-");
    }

    /**
     * 关于页面数据<br>
     * 返回结构（Map 的三个键）：
     * <ul>
     *   <li>{@code about}：富文本字符串，前端用 v-formatted-text 渲染</li>
     *   <li>{@code statistic}：统计指标列表，结构 [{@code label}, {@code value}]，前端渲染为指标卡片网格</li>
     *   <li>{@code thanks}：致谢列表，结构 [{@code thanks}]，前端渲染为列表</li>
     * </ul>
     */
    @GetMapping ("/about-page/{d}/{l}")
    public Map<String, Object> about(@PathVariable Dialect d, @PathVariable Language l)
    {
        Map<String, Object> map = new HashMap<>();

        map.put("about", aboutText(d, l));

        map.put("thanks", readJson(
                new ScTcText(KV.get("website-acknowledgement:" + d)).get(l).toString(),
                new TypeReference<List<String>>() {}
        ));

        map.put("statistic", aboutStats(d, l));

        return map;
    }

    private UString aboutText(Dialect d, Language l)
    {
        String s = String.format("""
                %s
                
                
                
                %s
                """, KV.get("website-about"), KV.get("website-about:" + d));
        return ScTcText.get(s, l);
    }

    private List<Twin<UString>> aboutStats(Dialect d, Language l)
    {
        var dialect = d.toString();
        var DName = d.getName().getTc();

        List<Twin<String>> list = List.of(
                Twin.of("項目立項已經", ChronoUnit.DAYS.between(LocalDate.of(2024, 10, 3), LocalDate.now()) + "天"),
                Twin.of("項目上綫已經", ChronoUnit.DAYS.between(LocalDate.of(2026, 5, 28), LocalDate.now()) + "天"),
                Twin.of("版本號", "1.9"),
                Twin.of("版本地標", "八一館"),
                Twin.of("收錄" + DName + "用漢字", hz.findRowCountInHanziTable(dialect) + "個"),
                Twin.of("收錄" + DName + "詞語", cy.findRowCountInCiyuTable(dialect) + "條"),
                Twin.of("電子化" + DName + "辭書", ck.findRowCountInReferTable(dialect) + "段")
        );
        return ListTool.mapping(list, i -> i.map(str -> ScTcText.get(str, l)));
    }
}