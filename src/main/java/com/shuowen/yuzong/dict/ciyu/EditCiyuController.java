package com.shuowen.yuzong.dict.ciyu;

import com.shuowen.yuzong.dict.ciyu.data.CiyuCreate;
import com.shuowen.yuzong.dict.ciyu.data.CiyuUpdate;
import com.shuowen.yuzong.dict.data.domain.Reference.DictCode;
import com.shuowen.yuzong.dict.data.dto.SearchResult;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.obfuscate.ObfInt;
import com.shuowen.yuzong.util.text.UString;
import com.shuowen.yuzong.util.tuple.APIResponse;
import com.shuowen.yuzong.util.tuple.Maybe;
import com.shuowen.yuzong.util.tuple.MaybeAPIResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/api/edit/ciyu")
public class EditCiyuController
{
    @Autowired
    CiyuService cy;

    @GetMapping ("/filter/{d}")
    public APIResponse<List<SearchResult>> filter(@PathVariable Dialect d, @RequestParam UString query)
    {
        return APIResponse.success(cy.getCiyuFilterInfo(query, d));
    }

    /**
     * 获得精确的某一个字的信息
     */
    @GetMapping ("/get-info/{d}")
    public APIResponse<CiyuUpdate> ciyufind(@PathVariable Dialect d, @RequestParam ObfInt id
    )
    {
        return MaybeAPIResponse.of(
                Maybe.uncertain(cy.getCiyuById(id.decode(), d)),
                "没有找到词语"
        );
    }

    /**
     * 提交编辑
     */
    @PostMapping ("/submit/{d}")
    public APIResponse<Void> edit(@PathVariable Dialect d, @RequestBody CiyuUpdate he)
    {
        cy.editCiyu(he, d);
        return APIResponse.success();
    }

    @PostMapping ("/create/{d}")
    public APIResponse<Void> createCiyu(@PathVariable Dialect d, @RequestBody CiyuCreate ce)
    {
        cy.createCiyu(ce, d);
        return APIResponse.success();
    }

    @GetMapping ("/get-link/{d}/{dict}")
    public APIResponse<ObfInt> getLink(
            @PathVariable Dialect d, @PathVariable DictCode dict,
            @RequestParam UString ciyu
    )
    {
        return MaybeAPIResponse.of(cy.getEditLinkIfExist(ciyu, d), "没有找到词语编辑链接");
    }
}
