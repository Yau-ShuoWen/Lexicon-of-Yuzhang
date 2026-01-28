package com.shuowen.yuzong.controller.edit;

import com.shuowen.yuzong.Tool.Obfuscation;
import com.shuowen.yuzong.Tool.RichTextUtil;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
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
            @PathVariable String dialect
    )
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
            @PathVariable String dictionary,
            @RequestParam String query
    )
    {
        try
        {
            return APIResponse.success(ck.findContent(dictionary, query));
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }

    @GetMapping ("/get-page/{dictionary}")
    public APIResponse<RefPage> getPage(
            @PathVariable String dictionary,
            @RequestParam String sort)
    {
        try
        {
            var ans = ck.getPageInfo(dictionary, Obfuscation.decode(sort));
            return APIResponse.success(ans.encode());
        } catch (Exception e)
        {
            return APIResponse.failure("获取失败。Fail to get.");
        }
    }

    @PostMapping ("/update-page/{dictionary}")
    public APIResponse<Void> updatePage(
            @PathVariable String dictionary,
            @RequestBody RefPage page)
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
            @RequestParam String sort, @RequestParam boolean before
    )
    {
        try
        {
            var ans = ck.insertPage(dictionary, Obfuscation.decode(sort), before);
            return APIResponse.success(ans);
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }

    @PostMapping ("/get-nearby/{dictionary}")
    public APIResponse<Pair<Maybe<String>, Maybe<String>>> getNearBy(
            @PathVariable String dictionary, @RequestBody Pair<String, String> sorts)
    {
        try
        {
            var decodeSorts = Pair.of(
                    Obfuscation.decode(sorts.getLeft()),
                    Obfuscation.decode(sorts.getRight())
            );
            return APIResponse.success(ck.getNearBy(dictionary, decodeSorts));
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }
}
