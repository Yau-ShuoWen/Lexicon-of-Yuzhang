package com.shuowen.yuzong.dict.controller.edit;

import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.obfuscate.ObfInt;
import com.shuowen.yuzong.dict.data.domain.Reference.DictCode;
import com.shuowen.yuzong.dict.data.domain.Word.CiyuCreate;
import com.shuowen.yuzong.dict.data.domain.Word.CiyuUpdate;
import com.shuowen.yuzong.dict.data.dto.SearchResult;
import com.shuowen.yuzong.dict.service.Word.CiyuService;
import com.shuowen.yuzong.util.text.UString;
import com.shuowen.yuzong.util.tuple.APIResponse;
import com.shuowen.yuzong.util.tuple.Maybe;
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
    public List<SearchResult> filter(@PathVariable Dialect d, @RequestParam UString query)
    {
        return cy.getCiyuFilterInfo(query, d);
    }

    /**
     * 获得精确的某一个字的信息
     */
    @GetMapping ("/get-info/{d}")
    public APIResponse<Maybe<CiyuUpdate>> ciyufind(@PathVariable Dialect d, @RequestParam ObfInt id
    )
    {
        try
        {
            return APIResponse.success(Maybe.uncertain(cy.getCiyuById(id.decode(), d)));
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }

    /**
     * 提交编辑
     */
    @PostMapping ("/submit/{d}")
    public APIResponse<Void> edit(@PathVariable Dialect d, @RequestBody CiyuUpdate he)
    {
        try
        {
            cy.editCiyu(he, d);
            return APIResponse.success();
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }

    @PostMapping ("/create/{d}")
    public APIResponse<Void> createCiyu(@PathVariable Dialect d, @RequestBody CiyuCreate ce)
    {
        try
        {
            cy.createCiyu(ce, d);
            return APIResponse.success();
        } catch (Exception e)
        {
            if (e instanceof IllegalArgumentException)
            {
            }
            else e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }

    @GetMapping ("/get-link/{d}/{dict}")
    public APIResponse<Maybe<ObfInt>> getLink(
            @PathVariable Dialect d, @PathVariable DictCode dict,
            @RequestParam UString ciyu
    )
    {
        try
        {
            return APIResponse.success(cy.getEditLinkIfExist(ciyu, d));
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }
}
