package com.shuowen.yuzong.controller.info;

import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.controller.APIResponse;
import com.shuowen.yuzong.data.mapper.Character.HanziMapper;
import com.shuowen.yuzong.data.mapper.Refer.ReferMapper;
import com.shuowen.yuzong.data.mapper.Word.WordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 获得字典信息
 */
@RestController
@RequestMapping ("/api/info")
public class GetInfoController
{
    @Autowired
    private HanziMapper hz;

    @Autowired
    private WordMapper cy;

    @Autowired
    private ReferMapper ck;

    @GetMapping ("{dialect}/get-info-number")
    public APIResponse<Map<String, Integer>> getInfoNumber(@PathVariable String dialect)
    {
        try
        {
            var d = Dialect.of(dialect).toString();
            return APIResponse.success(Map.of(
                    "hanzi_num", hz.findRowCountInHanziTable(d),
                    "pinyin_num", hz.findRowCountInPinyinTable(d),
                    "ciyu_num", cy.findRowCountInCiyuTable(d),
                    "refer_num", ck.findRowCountInReferTable(d)
            ));
        } catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }
}
