package com.shuowen.yuzong.controller.info;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.Tool.format.JsonTool;
import com.shuowen.yuzong.data.mapper.Character.HanziMapper;
import com.shuowen.yuzong.data.mapper.Reference.RefMapper;
import com.shuowen.yuzong.data.mapper.Word.CiyuMapper;
import com.shuowen.yuzong.service.impl.KV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

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

    @GetMapping ("/about-page/{d}/{l}")
    public Map<String, UString> about(@PathVariable Dialect d, @PathVariable Language l)
    {
        Map<String, UString> map = new HashMap<>();

        map.put("about", aboutText(d).get(l));
        map.put("thanks", new ScTcText(KV.get("website-acknowledgement:" + d)).get(l));
        map.put("statistic", aboutNumber(d).get(l));

        return map;
    }

    private ScTcText aboutText(Dialect d)
    {
        String s = String.format("""
                %s
                
                
                
                %s
                """, KV.get("website-about"), KV.get("website-about:" + d));
        return new ScTcText(s);
    }

    private ScTcText aboutNumber(Dialect d)
    {
        var dialect = d.toString();
        String s = String.format("""
                        - 項目立項已經：%s天
                        - 項目上綫已經：%s天
                        - 版本號：%s
                        ------
                        - 收錄%s用漢字：%s個
                        - 收錄%s詞語：%s條
                        - 電子化%s相關辭書：%s段
                        """,
                ChronoUnit.DAYS.between(LocalDate.of(2024, 10, 3), LocalDate.now()),
                ChronoUnit.DAYS.between(LocalDate.of(2026, 5, 28), LocalDate.now()),
                KV.get("website-version"),
                d.getName().getTc(), hz.findRowCountInHanziTable(dialect),
                d.getName().getTc(), cy.findRowCountInCiyuTable(dialect),
                d.getName().getTc(), ck.findRowCountInReferTable(dialect)
        );
        return new ScTcText(s);
    }

    @GetMapping ("/get-menu/{code}")
    public List<ScTcText> pronunTagList(@PathVariable String code)
    {
        return JsonTool.readJson(KV.get("types-of-pronunciation"),
                new TypeReference<>() {});
    }
}
