package com.shuowen.yuzong.controller.edit;

import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.format.ObfInt;
import com.shuowen.yuzong.controller.APIResponse;
import com.shuowen.yuzong.data.domain.Word.CiyuUpdate;
import com.shuowen.yuzong.data.dto.SearchResult;
import com.shuowen.yuzong.service.impl.Word.CiyuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping ("/api/edit/ciyu")
public class EditCiyuController
{
    @Autowired
    CiyuService cy;

    @GetMapping ("/filter/{d}")
    public List<SearchResult> filter(@PathVariable Dialect d, @RequestParam String query)
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

}
