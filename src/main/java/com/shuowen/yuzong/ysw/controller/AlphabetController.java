package com.shuowen.yuzong.ysw.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.json.JsonTool;
import com.shuowen.yuzong.util.ext.list.ListTool;
import com.shuowen.yuzong.util.text.ScTcText;
import com.shuowen.yuzong.util.text.UString;
import com.shuowen.yuzong.util.tuple.Pair;
import com.shuowen.yuzong.util.tuple.Twin;
import com.shuowen.yuzong.ysw.data.domain.AlphabetTransfer;
import com.shuowen.yuzong.ysw.data.domain.alphabet.AlphabetTable;
import com.shuowen.yuzong.ysw.data.mapper.AlphabetMapper;
import com.shuowen.yuzong.ysw.linguistic.Alphabet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping ("/api/alphabet/")
public class AlphabetController
{
    @Autowired
    AlphabetMapper m;

    @GetMapping ("/introduce/{a}/{l}")
    public Twin<UString> getIntroduce(@PathVariable Alphabet a, @PathVariable Language l)
    {
        return Twin.of(a.getName().get(l), ScTcText.get(m.getIntroduce(a.getCode()), l));
    }

    @GetMapping ("/table/new/{a}/{l}")
    public AlphabetTable getTheTable(@PathVariable Alphabet a, @PathVariable Language l)
    {
        return new AlphabetTable(m.getPinyinTable(a.getCode()), a, l);
    }

    @GetMapping ("/trans/{a}/{l}")
    public List<Pair<UString, String>> getTrans(@PathVariable Alphabet a, @PathVariable Language l)
    {
        return a.getTrans(l);
    }

    @GetMapping ("/transfer/{alphabet}/{l}")
    public String transfer(@PathVariable Alphabet alphabet, @PathVariable Language l,
                           @RequestParam String funName, @RequestParam String s
    )
    {
        return AlphabetTransfer.format(alphabet, l, funName, s);
    }

    @GetMapping ("/catalog/{l}")
    public List<Pair<String, List<Map<String, String>>>> getCatalog(@PathVariable Language l)
    {
        List<Pair<String, List<Map<String, String>>>> list =
                JsonTool.readJson(m.getPinyinTable("alphabet-catalog"), new TypeReference<>() {});

        return ListTool.mapping(list, i ->
        {
            var left = ScTcText.get(i.getLeft(), l);
            var right = ListTool.mapping(i.getRight(), item ->
                    Map.of(
                            "name", ScTcText.get(item.get("name"), l).toString(),
                            "example", ScTcText.get(item.get("example"), l).toString(),
                            "url", item.get("url")
                    )
            );
            return Pair.of(left.toString(), right);
        });
    }
}
