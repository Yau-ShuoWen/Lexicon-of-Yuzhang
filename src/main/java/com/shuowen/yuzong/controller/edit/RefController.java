package com.shuowen.yuzong.controller.edit;

import com.shuowen.yuzong.Tool.FractionIndex;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Twin;
import com.shuowen.yuzong.controller.APIResponse;
import com.shuowen.yuzong.data.domain.Reference.RefPage;
import com.shuowen.yuzong.data.dto.SearchResult;
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

    /**
     * 获取词典列表
     */
    @GetMapping ("/get-dictionaries/{dialect}")
    public List<Pair<String, String>> getDictionaries(
            @PathVariable String dialect)
    {
        return ck.getDictionaryMenu(Dialect.of(dialect));
    }

    /**
     * 富文本格式预览
     */
    @GetMapping ("/preview/{dialect}/{dictionary}")
    public APIResponse<String> preview(
            @PathVariable String dictionary, @PathVariable String dialect,
            @RequestParam String text)
    {

        return APIResponse.success(text);
    }

    /**
     * 查询包括搜索信息的句子<br>
     * 目前只做一个最简单的关键词查询
     */
    @GetMapping ("/find-content/{dictionary}")
    public APIResponse<List<SearchResult>> findContent(
            @PathVariable String dictionary, @RequestParam String query)
    {
        try
        {
            var ans = ck.findContent(dictionary, query);
            return APIResponse.success(ans);
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }

    @GetMapping ("/get-page/{dictionary}")
    public APIResponse<RefPage> getPage(
            @PathVariable String dictionary, @RequestParam FractionIndex sort)
    {
        try
        {
            var ans = RefPage.tryOf(ck.getPage(dictionary, sort.toString()));
            return (ans.isValid()) ?
                    APIResponse.success(ans.getValue()) :
                    APIResponse.failure("未找到数据。not found");
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }

    @PostMapping ("/update-page/{dictionary}")
    public APIResponse<Void> updatePage(
            @PathVariable String dictionary, @RequestBody RefPage page)
    {
        try
        {
            ck.updatePage(dictionary, page);
            return APIResponse.success();
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }

    }

    /**
     * 给出插入页数的位置，插入页数并且返回这个空的页数数据
     */
    @PostMapping ("/create-page/{dictionary}")
    public APIResponse<RefPage> createPage(
            @PathVariable String dictionary,
            @RequestParam FractionIndex sort, @RequestParam boolean before
    )
    {
        try
        {
            return APIResponse.success(ck.insertPage(dictionary, sort, before));
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }

    @PostMapping ("/get-nearby/{dictionary}")
    public APIResponse<Twin<Maybe<FractionIndex>>> getNearBy(
            @PathVariable String dictionary, @RequestBody Twin<FractionIndex> sorts)
    {
        try
        {
            return APIResponse.success(ck.getNearBy(dictionary, sorts));
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }

    @PostMapping ("/delete-page/{dictionary}")
    public APIResponse<RefPage> deletePage(
            @PathVariable String dictionary, @RequestParam FractionIndex frontSort)
    {
        try
        {
            return APIResponse.success(ck.deletePage(dictionary, frontSort));
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }
}
