package com.shuowen.yuzong.dict.controller.edit;

import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.dict.data.domain.Reference.DictCode;
import com.shuowen.yuzong.dict.data.domain.Reference.Page;
import com.shuowen.yuzong.dict.data.domain.Reference.RefDraft;
import com.shuowen.yuzong.dict.data.domain.Reference.RefProof;
import com.shuowen.yuzong.dict.data.dto.SearchResult;
import com.shuowen.yuzong.dict.service.Reference.DictService;
import com.shuowen.yuzong.dict.service.Reference.RefService;
import com.shuowen.yuzong.util.ext.sort.FractionIndex;
import com.shuowen.yuzong.util.text.UString;
import com.shuowen.yuzong.util.tuple.APIResponse;
import com.shuowen.yuzong.util.tuple.Pair;
import com.shuowen.yuzong.util.tuple.Twin;
import com.shuowen.yuzong.util.tuple.NearbyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/api/ref")
public class RefController
{
    @Autowired
    private RefService ck;

    @Autowired
    private DictService dict;

    /**
     * 获取词典列表
     */
    @GetMapping ("/get-dictionaries/{d}")
    public APIResponse<List<Pair<UString, DictCode>>> getDictionaries(@PathVariable Dialect d)
    {
        return APIResponse.success(dict.getDictionaryMenu(d));
    }

    /**
     * 查询包括搜索信息的句子<br>
     * 目前只做一个最简单的关键词查询
     */
    @GetMapping ("/find-content/{dict}")
    public APIResponse<List<SearchResult>> findContent(
            @PathVariable DictCode dict, @RequestParam String query)
    {
        return APIResponse.success(ck.findContent(dict, query));
    }

    @GetMapping ("/get-page/{dict}")
    public APIResponse<Page> getPage(
            @PathVariable DictCode dict, @RequestParam FractionIndex sort)
    {
        return APIResponse.success(ck.getRefPage(dict, sort));
    }

    @PostMapping ("/update-page/{dict}")
    public APIResponse<Void> updatePage(
            @PathVariable DictCode dict, @RequestBody RefDraft page)
    {
        ck.overwritePage(dict, page);
        return APIResponse.success();
    }

    /**
     * 锁定页面，因为不可以随便触发，所以用post
     */
    @PostMapping ("/lock-page/{dict}")
    public APIResponse<RefProof> lockPage(@PathVariable DictCode dict, @RequestParam FractionIndex sort)
    {
        return APIResponse.success(ck.lockPage(dict, sort));
    }

    /**
     * 给出插入页数的位置，插入页数并且返回这个空的页数数据
     */
    @PostMapping ("/create-page/{dict}")
    public APIResponse<RefDraft> createPage(
            @PathVariable DictCode dict, @RequestParam FractionIndex sort,
            @RequestParam boolean before)
    {
        return APIResponse.success(ck.insertPage(dict, sort, before));
    }

    @PostMapping ("/edit-page/{dict}")
    public APIResponse<Void> editPage(@PathVariable DictCode dict, @RequestBody RefProof page)
    {
        ck.editPage(dict, page);
        return APIResponse.success();
    }

    @PostMapping ("/get-nearby/{dict}")
    public APIResponse<NearbyResponse<FractionIndex>> getNearBy(
            @PathVariable DictCode dict, @RequestBody Twin<FractionIndex> sorts)
    {
        return APIResponse.success(NearbyResponse.from(ck.getNearBy(dict, sorts)));
    }

    @PostMapping ("/delete-page/{dict}")
    public APIResponse<Page> deletePage(
            @PathVariable DictCode dict, @RequestParam FractionIndex frontSort)
    {
        return APIResponse.success(ck.deletePage(dict, frontSort));
    }

    @GetMapping ("/get-catalog/{dict}")
    public APIResponse<List<Pair<FractionIndex, String>>> getCatalog(@PathVariable DictCode dict)
    {
        return APIResponse.success(ck.getCatalog(dict));
    }
}
