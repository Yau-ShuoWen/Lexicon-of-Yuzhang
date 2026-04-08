package com.shuowen.yuzong.controller.edit;

import com.shuowen.yuzong.Tool.FractionIndex;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Twin;
import com.shuowen.yuzong.controller.APIResponse;
import com.shuowen.yuzong.data.domain.Reference.DictCode;
import com.shuowen.yuzong.data.domain.Reference.Page;
import com.shuowen.yuzong.data.domain.Reference.RefDraft;
import com.shuowen.yuzong.data.domain.Reference.RefProof;
import com.shuowen.yuzong.data.dto.SearchResult;
import com.shuowen.yuzong.service.impl.Reference.DictService;
import com.shuowen.yuzong.service.impl.Reference.RefService;
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
    public List<Pair<String, DictCode>> getDictionaries(@PathVariable Dialect d)
    {
        return dict.getDictionaryMenu(d);
    }

    /**
     * 查询包括搜索信息的句子<br>
     * 目前只做一个最简单的关键词查询
     */
    @GetMapping ("/find-content/{dict}")
    public APIResponse<List<SearchResult>> findContent(
            @PathVariable DictCode dict, @RequestParam String query)
    {
        try
        {
            var ans = ck.findContent(dict, query);
            return APIResponse.success(ans);
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }

    @GetMapping ("/get-page/{dict}")
    public APIResponse<Page> getPage(
            @PathVariable DictCode dict, @RequestParam FractionIndex sort)
    {
        try
        {
            return APIResponse.success(ck.getRefPage(dict, sort));
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }

    @PostMapping ("/update-page/{dict}")
    public APIResponse<Void> updatePage(
            @PathVariable DictCode dict, @RequestBody RefDraft page)
    {
        try
        {
            ck.overwritePage(dict, page);
            return APIResponse.success();
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }

    /**
     * 锁定页面，因为不可以随便触发，所以用post
     */
    @PostMapping ("/lock-page/{dict}")
    public APIResponse<RefProof> lockPage(@PathVariable DictCode dict, @RequestParam FractionIndex sort)
    {
        try
        {
            return APIResponse.success(ck.lockPage(dict, sort));
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }

    /**
     * 给出插入页数的位置，插入页数并且返回这个空的页数数据
     */
    @PostMapping ("/create-page/{dict}")
    public APIResponse<RefDraft> createPage(
            @PathVariable DictCode dict, @RequestParam FractionIndex sort,
            @RequestParam boolean before)
    {
        try
        {
            return APIResponse.success(ck.insertPage(dict, sort, before));
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }

    @PostMapping ("/edit-page/{dict}")
    public APIResponse<Void> editPage(@PathVariable DictCode dict, @RequestBody RefProof page)
    {
        try
        {
            ck.editPage(dict, page);
            return APIResponse.success();
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }

    @PostMapping ("/get-nearby/{dict}")
    public APIResponse<Twin<Maybe<FractionIndex>>> getNearBy(
            @PathVariable DictCode dict, @RequestBody Twin<FractionIndex> sorts)
    {
        try
        {
            return APIResponse.success(ck.getNearBy(dict, sorts));
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }

    @PostMapping ("/delete-page/{dict}")
    public APIResponse<Page> deletePage(
            @PathVariable DictCode dict, @RequestParam FractionIndex frontSort)
    {
        try
        {
            return APIResponse.success(ck.deletePage(dict, frontSort));
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }

    @GetMapping ("/get-catalog/{dict}")
    public APIResponse<List<Pair<FractionIndex, String>>> getCatalog(@PathVariable DictCode dict)
    {
        try
        {
            return APIResponse.success(ck.getCatalog(dict));
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }
}
